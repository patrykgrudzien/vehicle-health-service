package me.grudzien.patryk.integration.registration.resource;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.persistence.EntityManager;

import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;
import me.grudzien.patryk.registration.repository.CustomUserRepository;
import me.grudzien.patryk.registration.repository.EmailVerificationTokenRepository;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.NO_EXISTING_EMAIL;
import static me.grudzien.patryk.TestsUtils.NO_EXISTING_EMAIL_1;
import static me.grudzien.patryk.TestsUtils.NO_EXISTING_EMAIL_2;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;
import static me.grudzien.patryk.TestsUtils.prepareRegistrationJSONBody;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext
class RegistrationResourceImplIT extends BaseRegistrationResource {

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    void tearDown() {
        emailVerificationTokenRepository.deleteAll();
        customUserRepository.deleteAll();
    }

    private static Stream<Arguments> registrationSuccessfulTestData() throws JsonProcessingException {
        // given
        final String registrationJSONBody = prepareRegistrationJSONBody("John", "Snow", NO_EXISTING_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);
        final String registrationJSONBody1 = prepareRegistrationJSONBody("John", "Snow", NO_EXISTING_EMAIL_1, TEST_PASSWORD, ENABLE_ENCODING);

        return Stream.of(
                arguments("en", NO_EXISTING_EMAIL, registrationJSONBody,
                                    String.format("Thank you for registration! Check (%s) to confirm newly created account.", NO_EXISTING_EMAIL)),
                arguments("pl", NO_EXISTING_EMAIL_1, registrationJSONBody1,
                                    String.format("Dziękuję! Rejestracja przebiegła pomyślnie. Sprawdź (%s) aby potwierdzić nowo utworzone konto.", NO_EXISTING_EMAIL_1))
        );
    }

    @DisplayName("Register user account. Successful. 200 OK.")
    @ParameterizedTest(name = "Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationSuccessfulTestData")
    void registerUserAccount_successful_differentLanguageHeader(final String language, final String email, final String jsonBody, final String responseMessage) {
        // then
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(Method.POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.OK.value())
               .body(notNullValue())
               .body("message", is(responseMessage))
               .body("successful", nullValue())
               .body("redirectionUrl", nullValue());

        final CustomUser user = customUserRepository.findByEmail(email);
        final EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByCustomUser_Email(email);
        Assertions.assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertFalse(user.isEnabled()),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNotNull(),
                () -> assertThat(emailVerificationToken).isNotNull(),
                () -> assertEquals(emailVerificationToken.getCustomUser(), user)
        );
    }

    private static Stream<Arguments> registrationFailedEmailExistTestData() throws JsonProcessingException {
        // given
        final String registrationJSONBody = prepareRegistrationJSONBody("John", "Snow", TEST_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);

        return Stream.of(
                arguments("en", registrationJSONBody,
                                    String.format("User with specified email (%s) already exists!", TEST_EMAIL)),
                arguments("pl", registrationJSONBody,
                                    String.format("Użytkownik o podanym adresie e-mail (%s) już istnieje!", TEST_EMAIL))
        );
    }

    @DisplayName("Register user account. Failed. Email already exists! 400 Bad request!")
    @ParameterizedTest(name = "Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationFailedEmailExistTestData")
    void registerUserAccount_failed_emailExist_differentLanguageHeader(final String language, final String jsonBody, final String responseMessage) {
        // then
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(Method.POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body(notNullValue())
               .body("message", is(responseMessage))
               .body("successful", nullValue())
               .body("redirectionUrl", nullValue());

        final CustomUser user = customUserRepository.findByEmail(TEST_EMAIL);
        Assertions.assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertTrue(user.isEnabled()),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(TEST_EMAIL)).isNull()
        );
    }

    private static Stream<Arguments> registrationFailedValidationErrorsTestData() throws JsonProcessingException {
        // given
        final String emptyEmail = prepareRegistrationJSONBody("John", "Snow", StringUtils.EMPTY, TEST_PASSWORD, ENABLE_ENCODING);
        final String invalidEmailFormat = prepareRegistrationJSONBody("John", "Snow", "invalid-email-format", TEST_PASSWORD, ENABLE_ENCODING);
        final String emptyPassword = prepareRegistrationJSONBody("John", "Snow", NO_EXISTING_EMAIL_2, StringUtils.EMPTY, ENABLE_ENCODING);
        final String noCredentialsProvided = prepareRegistrationJSONBody(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, ENABLE_ENCODING);
        final String emailsDoNotMatch = prepareRegistrationJSONBody("John", "Snow", "bad-email-3@gmail.com", "bad-email-4@gmail.com",
                                                                    TEST_PASSWORD, TEST_PASSWORD, ENABLE_ENCODING);
        final String passwordsDoNotMatch = prepareRegistrationJSONBody("John", "Snow", "bad-email-5@gmail.com", "bad-email-5@gmail.com",
                                                                       "password", "test", ENABLE_ENCODING);
        return Stream.of(
                arguments("en", emptyEmail, "Cannot save user. Validation errors in registration form!", 3, StringUtils.EMPTY,
                                    new String[]{"Provided email has incorrect format.", "Confirmed email address cannot be empty.", "Email address cannot be empty."}),
                arguments("pl", emptyEmail, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 3, StringUtils.EMPTY,
                                    new String[]{"Wprowadzony email ma nieprawidłowy format.", "Potwierdzający adres email nie może być pusty.", "Adres email nie może być pusty."}),
                arguments("en", invalidEmailFormat, "Cannot save user. Validation errors in registration form!", 1, "invalid-email-format",
                                    new String[]{"Provided email has incorrect format."}),
                arguments("pl", invalidEmailFormat, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 1, "invalid-email-format",
                                    new String[]{"Wprowadzony email ma nieprawidłowy format."}),
                arguments("en", emptyPassword, "Cannot save user. Validation errors in registration form!", 2, NO_EXISTING_EMAIL_2,
                                    new String[]{"Password cannot be empty.", "Confirmed password cannot be empty."}),
                arguments("pl", emptyPassword, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 2, NO_EXISTING_EMAIL_2,
                                    new String[]{"Hasło nie może być puste.", "Hasło potwierdzające nie może być puste."}),
                arguments("en", noCredentialsProvided, "Cannot save user. Validation errors in registration form!", 7, StringUtils.EMPTY,
                                    new String[]{"Last name cannot be empty.", "Email address cannot be empty.", "Provided email has incorrect format.",
                                            "Confirmed password cannot be empty.", "Confirmed email address cannot be empty.", "First name cannot be empty.",
                                            "Password cannot be empty."}),
                arguments("pl", noCredentialsProvided, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 7, StringUtils.EMPTY,
                                    new String[]{"Adres email nie może być pusty.", "Nazwisko nie może być puste.", "Hasło potwierdzające nie może być puste.",
                                            "Hasło nie może być puste.", "Wprowadzony email ma nieprawidłowy format.", "Potwierdzający adres email nie może być pusty.",
                                            "Imię nie może być puste."}),
                arguments("en", emailsDoNotMatch, "Cannot save user. Validation errors in registration form!", 1, "bad-email-3@gmail.com",
                                    new String[]{"The email fields must match."}),
                arguments("pl", emailsDoNotMatch, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 1, "bad-email-3@gmail.com",
                                    new String[]{"Pola z adresami email muszą być identyczne."}),
                arguments("en", passwordsDoNotMatch, "Cannot save user. Validation errors in registration form!", 1, "bad-email-5@gmail.com",
                                    new String[]{"The password fields must match."}),
                arguments("pl", passwordsDoNotMatch, "Nie można zapisać użytkownika. Formularz rejestracyjny zawiera błędy!", 1, "bad-email-5@gmail.com",
                                    new String[]{"Pola z hasłami muszą być identyczne."})
        );
    }

    @DisplayName("Register user account. Failed. Validation errors! 400 Bad request!")
    @ParameterizedTest(name = "Response message created using ({0}) \"Language\" header.")
    @MethodSource("registrationFailedValidationErrorsTestData")
    void registerUserAccount_failed_validationErrors_differentLanguageHeader(final String language, final String jsonBody, final String errorMessage,
                                                                             final int errorsSize, final String email, final String... errorItems) {
        given().log().all()
               .header("Language", language)
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(Method.POST, REGISTRATION_CREATE_USER_ACCOUNT_URI)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body(notNullValue())
               .body("message", equalTo(errorMessage))
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems))
               .body("successful", nullValue())
               .body("redirectionUrl", nullValue());

        final CustomUser user = customUserRepository.findByEmail(email);
        Assertions.assertAll(
                () -> assertThat(user).isNull(),
                () -> assertFalse(Optional.ofNullable(user).map(CustomUser::isEnabled).orElse(false)),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser(user)).isNull(),
                () -> assertThat(emailVerificationTokenRepository.findByCustomUser_Email(email)).isNull()
        );
    }

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

        /**
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
         */
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
}
