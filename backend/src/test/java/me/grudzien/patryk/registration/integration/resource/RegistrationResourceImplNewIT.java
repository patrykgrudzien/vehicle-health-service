package me.grudzien.patryk.registration.integration.resource;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.registration.AbstractRegistrationResourceHelper;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.POST;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
class RegistrationResourceImplNewIT extends AbstractRegistrationResourceHelper {

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        RestAssured.baseURI = HTTP_LOCALHOST;
    }

    private static Stream<Arguments> registrationSuccessfulMethodSource() {
	    return Stream.of(
	            arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .email(NO_EXISTING_EMAIL)
                                .jsonBody(createUserRegistrationDtoJson(NO_EXISTING_EMAIL, TEST_PASSWORD).doEncoding(true))
                                .responseMessage(format("Thank you for registration! Check (%s) to confirm newly created account.", NO_EXISTING_EMAIL))
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .email(NO_EXISTING_EMAIL_1)
                                .jsonBody(createUserRegistrationDtoJson(NO_EXISTING_EMAIL_1, TEST_PASSWORD).doEncoding(true))
                                .responseMessage(format("Dziękuję! Rejestracja przebiegła pomyślnie. Sprawdź (%s) aby potwierdzić nowo utworzone konto.", NO_EXISTING_EMAIL_1))
                                .build()
                )
        );
    }

    @DisplayName("Register user account. Successful. 200 OK.")
    @MethodSource("registrationSuccessfulMethodSource")
    @ParameterizedTest
    void shouldReturn200onCreateUserAccountWithoutSendingEmailLanguageEnAndPl(final RegistrationResourceTestDataBuilder builder) {
        final String email = builder.getEmail();
        given().log().all()
               .header("Language", builder.getLanguage())
               .with().body(builder.getJsonBody())
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(OK.value())
               .body(notNullValue())
               .body("$", hasKey("message"))
               .body("message", is(builder.getResponseMessage()))
               .body("$", not(hasKey("securityStatus")))
               .body("$", not(hasKey("accountStatus")))
               .body("$", not(hasKey("lastRequestedPath")))
               .body("$", not(hasKey("lastRequestMethod")));

        final CustomUser user = customUserRepository.findByEmail(email);
	    assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertThat(user.isEnabled()).isFalse(),
                () -> assertThat(user.isHasFakeEmail()).isTrue(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(email)).isNull()
        );
	    emailVerificationTokenRepository.deleteAll();
        customUserRepository.deleteAll();
    }

    private static Stream<Arguments> registrationFailedEmailExistMethodSource() {
	    return Stream.of(
	            arguments(
	                    RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(createUserRegistrationDtoJson(TEST_EMAIL, TEST_PASSWORD).doEncoding(true))
                                .responseMessage(format("User with specified email (%s) already exists!", TEST_EMAIL))
                                .shouldDelete(false)
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(createUserRegistrationDtoJson(TEST_EMAIL, TEST_PASSWORD).doEncoding(true))
                                .responseMessage(format("Użytkownik o podanym adresie e-mail (%s) już istnieje!", TEST_EMAIL))
                                .shouldDelete(true)
                                .build()
                )
        );
    }

    @DisplayName("Register user account. Failed. Email already exists! 400 Bad request!")
    @MethodSource("registrationFailedEmailExistMethodSource")
    @ParameterizedTest
    void shouldReturn400onCreateUserAccountWhenEmailExistsLanguageEnAndPl(final RegistrationResourceTestDataBuilder builder) {
        given().log().all()
               .header("Language", builder.getLanguage())
               .with().body(builder.getJsonBody())
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(BAD_REQUEST.value())
               .body(notNullValue())
               .body("$", hasKey("message"))
               .body("message", is(builder.getResponseMessage()))
               .body("$", not(hasKey("securityStatus")))
               .body("$", not(hasKey("accountStatus")))
               .body("$", not(hasKey("lastRequestedPath")))
               .body("$", not(hasKey("lastRequestMethod")));

        final CustomUser user = customUserRepository.findByEmail(TEST_EMAIL);
        assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertTrue(user.isEnabled()),
                () -> assertThat(user.isHasFakeEmail()).isTrue(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(TEST_EMAIL)).isNull()
        );
        // don't want to perform deleteAll() after first test for "en" language header
	    if (builder.isShouldDelete()) {
		    emailVerificationTokenRepository.deleteAll();
		    customUserRepository.deleteAll();
	    }
    }

    private static Stream<Arguments> registrationFailedValidationErrorsMethodSource() {
        final String emptyEmail = createUserRegistrationDtoJson(EMPTY_EMAIL, TEST_PASSWORD).doEncoding(true);
        final String invalidEmailFormat = createUserRegistrationDtoJson("invalid-email-format", TEST_PASSWORD).doEncoding(true);
        final String emptyPassword = createUserRegistrationDtoJson(NO_EXISTING_EMAIL_2, EMPTY_PASSWORD).doEncoding(true);
        final String noCredentialsProvided = createUserRegistrationDtoJson(
        		EMPTY_FIRST_NAME, EMPTY_LAST_NAME, EMPTY_EMAIL, EMPTY_EMAIL, EMPTY_PASSWORD, EMPTY_PASSWORD).doEncoding(true);
        final String emailsDoNotMatch = createUserRegistrationDtoJson(
        		"bad-email-3@gmail.com", "bad-email-4@gmail.com", TEST_PASSWORD).doEncoding(true);
	    final String passwordsDoNotMatch = createUserRegistrationDtoJson(
	    		"bad-email-5@gmail.com", "bad-email-5@gmail.com", "password", "test").doEncoding(true);

        return Stream.of(
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(emptyEmail)
                                .errorsSize(3)
                                .email(EMPTY)
                                .errorItems(new String[]{
                                        "Provided email has incorrect format.",
                                        "Confirmed email address cannot be empty.",
                                        "Email address cannot be empty."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(emptyEmail)
                                .errorsSize(3)
                                .email(EMPTY)
                                .errorItems(new String[]{
                                        "Wprowadzony email ma nieprawidłowy format.",
                                        "Potwierdzający adres email nie może być pusty.",
                                        "Adres email nie może być pusty."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(invalidEmailFormat)
                                .errorsSize(1)
                                .email("invalid-email-format")
                                .errorItems(new String[]{"Provided email has incorrect format."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(invalidEmailFormat)
                                .errorsSize(1)
                                .email("invalid-email-format")
                                .errorItems(new String[]{"Wprowadzony email ma nieprawidłowy format."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(emptyPassword)
                                .errorsSize(2)
                                .email(NO_EXISTING_EMAIL_2)
                                .errorItems(new String[]{
                                        "Password cannot be empty.",
                                        "Confirmed password cannot be empty."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(emptyPassword)
                                .errorsSize(2)
                                .email(NO_EXISTING_EMAIL_2)
                                .errorItems(new String[]{
                                        "Hasło nie może być puste.",
                                        "Hasło potwierdzające nie może być puste."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(noCredentialsProvided)
                                .errorsSize(7)
                                .email(EMPTY)
                                .errorItems(new String[]{
                                        "Last name cannot be empty.",
                                        "Email address cannot be empty.",
                                        "Provided email has incorrect format.",
                                        "Confirmed password cannot be empty.",
                                        "Confirmed email address cannot be empty.",
                                        "First name cannot be empty.",
                                        "Password cannot be empty."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(noCredentialsProvided)
                                .errorsSize(7)
                                .email(EMPTY)
                                .errorItems(new String[]{
                                        "Adres email nie może być pusty.",
                                        "Nazwisko nie może być puste.",
                                        "Hasło potwierdzające nie może być puste.",
                                        "Hasło nie może być puste.",
                                        "Wprowadzony email ma nieprawidłowy format.",
                                        "Potwierdzający adres email nie może być pusty.",
                                        "Imię nie może być puste."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(emailsDoNotMatch)
                                .errorsSize(1)
                                .email("bad-email-3@gmail.com")
                                .errorItems(new String[]{"The email fields must match."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(emailsDoNotMatch)
                                .errorsSize(1)
                                .email("bad-email-3@gmail.com")
                                .errorItems(new String[]{"Pola z adresami email muszą być identyczne."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("en")
                                .jsonBody(passwordsDoNotMatch)
                                .errorsSize(1)
                                .email("bad-email-5@gmail.com")
                                .errorItems(new String[]{"The password fields must match."})
                                .build()
                ),
                arguments(
                        RegistrationResourceTestDataBuilder.builder()
                                .language("pl")
                                .jsonBody(passwordsDoNotMatch)
                                .errorsSize(1)
                                .email("bad-email-5@gmail.com")
                                .errorItems(new String[]{"Pola z hasłami muszą być identyczne."})
                                .build()
                )
        );
    }

    @DisplayName("Register user account. Failed. Validation errors! 400 Bad request!")
    @MethodSource("registrationFailedValidationErrorsMethodSource")
    @ParameterizedTest
    void shouldReturn400onCreateUserAccountWhenValidationErrorsLanguageEnAndPl(final RegistrationResourceTestDataBuilder builder) {
        final String email = builder.getEmail();
        given().log().all()
               .header("Language", builder.getLanguage())
               .with().body(builder.getJsonBody())
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(BAD_REQUEST.value())
               .body(notNullValue())
               .body("errors", hasSize(builder.getErrorsSize()))
               .body("errors", hasItems(builder.getErrorItems()))
               .body("messageCode", equalTo("registration-form-validation-errors"))
               .body("message", notNullValue())
               .body("securityStatus", nullValue())
               .body("accountStatus", nullValue())
               .body("lastRequestedPath", nullValue())
               .body("lastRequestedMethod", nullValue());

        final CustomUser user = customUserRepository.findByEmail(email);
        assertAll(
                () -> assertThat(user).isNull(),
                () -> assertFalse(Optional.ofNullable(user).map(CustomUser::isEnabled).orElse(false)),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(email)).isNull()
        );
    }

    @Test
    @DisplayName("Confirm registration failed. Response status: 400 Bad Request - Email Verification Token is empty!")
    void shouldReturn400onConfirmRegistrationWhenTokenIsEmpty() {
        // when - then
        final ValidatableResponse response = given()
                .log().body(true)
                .header("Language", "en")
                .contentType(JSON).accept(JSON)
                .when()
                .get(REGISTRATION_CONFIRM_REGISTRATION_URI.apply(EMPTY))
                .then().log().body(true)
                .assertThat()
                .statusCode(BAD_REQUEST.value())
                .body("$", hasKey("message"))
                .body("message", is("email-verification-token-cannot-be-empty"));
        assertThat(response.extract().jsonPath().getMap("$", String.class, String.class)).hasSize(1);
    }
}
