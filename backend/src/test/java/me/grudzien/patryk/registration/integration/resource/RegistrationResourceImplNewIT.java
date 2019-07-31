package me.grudzien.patryk.registration.integration.resource;

import io.restassured.RestAssured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        RestAssured.baseURI = HTTP_LOCALHOST;
    }

    private static Stream<Arguments> registrationSuccessfulMethodSource() {
	    return Stream.of(
                arguments("en",
                          NO_EXISTING_EMAIL,
                          createUserRegistrationDtoJson(NO_EXISTING_EMAIL, TEST_PASSWORD).doEncoding(true),
                          String.format("Thank you for registration! Check (%s) to confirm newly created account.", NO_EXISTING_EMAIL)),
                arguments("pl",
                          NO_EXISTING_EMAIL_1,
                          createUserRegistrationDtoJson(NO_EXISTING_EMAIL_1, TEST_PASSWORD).doEncoding(true),
                          String.format("Dziękuję! Rejestracja przebiegła pomyślnie. Sprawdź (%s) aby potwierdzić nowo utworzone konto.", NO_EXISTING_EMAIL_1))
        );
    }

    @DisplayName("Register user account. Successful. 200 OK.")
    @ParameterizedTest(name = "{index}. Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationSuccessfulMethodSource")
    void shouldReturn200onCreateUserAccountWithoutSendingEmailLanguageEnAndPl(final String language, final String email,
                                                                              final String jsonBody, final String responseMessage) {
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(OK.value())
               .body(notNullValue())
               .body("$", hasKey("message"))
               .body("message", is(responseMessage))
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
                arguments("en",
                          createUserRegistrationDtoJson(TEST_EMAIL, TEST_PASSWORD).doEncoding(true),
                          String.format("User with specified email (%s) already exists!", TEST_EMAIL),
                          false),
                arguments("pl",
                          createUserRegistrationDtoJson(TEST_EMAIL, TEST_PASSWORD).doEncoding(true),
                          String.format("Użytkownik o podanym adresie e-mail (%s) już istnieje!", TEST_EMAIL),
                          true)
        );
    }

    @DisplayName("Register user account. Failed. Email already exists! 400 Bad request!")
    @ParameterizedTest(name = "{index}. Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationFailedEmailExistMethodSource")
    void shouldReturn400onCreateUserAccountWhenEmailExistsLanguageEnAndPl(final String language, final String jsonBody,
                                                                          final String responseMessage, final boolean shouldDelete) {
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(BAD_REQUEST.value())
               .body(notNullValue())
               .body("$", hasKey("message"))
               .body("message", is(responseMessage))
               .body("$", not(hasKey("securityStatus")))
               .body("$", not(hasKey("accountStatus")))
               .body("$", not(hasKey("lastRequestedPath")))
               .body("$", not(hasKey("lastRequestMethod")));

        final CustomUser user = customUserRepository.findByEmail(TEST_EMAIL);
        Assertions.assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertTrue(user.isEnabled()),
                () -> assertThat(user.isHasFakeEmail()).isTrue(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(TEST_EMAIL)).isNull()
        );
        // don't want to perform deleteAll() after first test for "en" language header
	    if (shouldDelete) {
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
                arguments("en", emptyEmail, 3, EMPTY,
                          new String[]{"Provided email has incorrect format.", "Confirmed email address cannot be empty.",
                                       "Email address cannot be empty."}),
                arguments("pl", emptyEmail, 3, EMPTY,
                          new String[]{"Wprowadzony email ma nieprawidłowy format.",
                                       "Potwierdzający adres email nie może być pusty.",
                                       "Adres email nie może być pusty."}),
                arguments("en", invalidEmailFormat, 1, "invalid-email-format",
                          new String[]{"Provided email has incorrect format."}),
                arguments("pl", invalidEmailFormat, 1, "invalid-email-format",
                          new String[]{"Wprowadzony email ma nieprawidłowy format."}),
                arguments("en", emptyPassword, 2, NO_EXISTING_EMAIL_2,
                          new String[]{"Password cannot be empty.", "Confirmed password cannot be empty."}),
                arguments("pl", emptyPassword, 2, NO_EXISTING_EMAIL_2,
                          new String[]{"Hasło nie może być puste.", "Hasło potwierdzające nie może być puste."}),
                arguments("en", noCredentialsProvided, 7, EMPTY,
                          new String[]{"Last name cannot be empty.", "Email address cannot be empty.",
                                       "Provided email has incorrect format.", "Confirmed password cannot be empty.",
                                       "Confirmed email address cannot be empty.", "First name cannot be empty.",
                                       "Password cannot be empty."}),
                arguments("pl", noCredentialsProvided, 7, EMPTY,
                          new String[]{"Adres email nie może być pusty.", "Nazwisko nie może być puste.",
                                       "Hasło potwierdzające nie może być puste.", "Hasło nie może być puste.",
                                       "Wprowadzony email ma nieprawidłowy format.", "Potwierdzający adres email nie może być pusty.",
                                       "Imię nie może być puste."}),
                arguments("en", emailsDoNotMatch, 1, "bad-email-3@gmail.com",
                          new String[]{"The email fields must match."}),
                arguments("pl", emailsDoNotMatch, 1, "bad-email-3@gmail.com",
                          new String[]{"Pola z adresami email muszą być identyczne."}),
                arguments("en", passwordsDoNotMatch, 1, "bad-email-5@gmail.com",
                          new String[]{"The password fields must match."}),
                arguments("pl", passwordsDoNotMatch, 1, "bad-email-5@gmail.com",
                          new String[]{"Pola z hasłami muszą być identyczne."})
        );
    }

    @DisplayName("Register user account. Failed. Validation errors! 400 Bad request!")
    @ParameterizedTest(name = "{index}. Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationFailedValidationErrorsMethodSource")
    void shouldReturn400onCreateUserAccountWhenValidationErrorsLanguageEnAndPl(final String language, final String jsonBody,
                                                                               final int errorsSize, final String email,
                                                                               final String... errorItems) {
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat().statusCode(BAD_REQUEST.value())
               .body(notNullValue())
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems))
               .body("messageCode", equalTo("registration-form-validation-errors"))
               .body("message", notNullValue())
               .body("securityStatus", nullValue())
               .body("accountStatus", nullValue())
               .body("lastRequestedPath", nullValue())
               .body("lastRequestedMethod", nullValue());

        final CustomUser user = customUserRepository.findByEmail(email);
        Assertions.assertAll(
                () -> assertThat(user).isNull(),
                () -> assertFalse(Optional.ofNullable(user).map(CustomUser::isEnabled).orElse(false)),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(email)).isNull()
        );
    }

    /*

    @Test
    void confirmRegistration_successful() throws JsonProcessingException {
        // given
        registerTestUser();
        final EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByCustomUser_Email(NO_EXISTING_EMAIL);
        final String confirmationEndpoint = REGISTRATION_CONFIRM_REGISTRATION_URI.apply(emailVerificationToken.getToken());

        // then
        given().header("Language", "en")
               .with()
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .redirects().follow(false)
               .when()
               .request(Method.GET, confirmationEndpoint)
               .then()
               .assertThat()
               .statusCode(HttpStatus.MOVED_PERMANENTLY.value())
               .body("message", is("Your account has been confirmed and activated!"))
               .header(HttpHeaders.LOCATION, equalTo("http://localhost:8080/ui/registration-confirmed"));

        *//**
         * If your test is @Transactional, it rolls back the transaction at the end of each test method by default. However,
         * as using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment,
         * the HTTP client and server run in separate threads and, thus, in separate transactions.
         * Any transaction initiated on the server does not roll back in this case.
         *
         * Since the test transaction is separate from the HTTP server transaction, the controller won't see changes made from within the test
         * method until the test transaction is actually committed. Conversely, you won't be able to roll back changes made as a result to the server call.
         *
         * You will seriously make your life easier by providing a mock implementation for whatever service/repository your controller uses.
         * Alternatively, you could use a tool like DBUnit to setup and tear down the database around each test case.
         *//*
        entityManager.flush();
        entityManager.clear();

        final CustomUser user = customUserRepository.findByEmail(NO_EXISTING_EMAIL);
        Assertions.assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertTrue(user.isEnabled()),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(NO_EXISTING_EMAIL)).isNull()
        );
    }

    private void registerTestUser() throws JsonProcessingException {
        final String jsonBody = prepareRegistrationJSONBody("John", "Snow", NO_EXISTING_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);
        given().header("Language", "en")
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(Method.POST, REGISTRATION_CREATE_USER_ACCOUNT_URI);
    }

    //    @Test
//    void resendEmailVerificationToken() {
//
//    }

*/
}
