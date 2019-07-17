package me.grudzien.patryk.integration.controller.login;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Objects;
import java.util.stream.Stream;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;
import static me.grudzien.patryk.TestsUtils.prepareAuthJSONBody;
import static me.grudzien.patryk.TestsUtils.prepareLoginRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class UserAuthenticationControllerIT {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int localServerPort;

	private static final String LOGIN_ENDPOINT = "/api/auth";

	@BeforeEach
	void setUp() {
		RestAssured.port = localServerPort;
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	@DisplayName("Login successful. 200 OK.")
	void testLoginSuccessful() {
        // given
		final JwtAuthenticationRequest loginRequest = prepareLoginRequest(TEST_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);

		// when
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity(LOGIN_ENDPOINT, loginRequest, JwtAuthenticationResponse.class);

		// then
		Assertions.assertAll(
                () -> Assertions.assertNotNull(response.getBody()),
                () -> Assertions.assertNotNull(Objects.requireNonNull(response.getBody()).getAccessToken()),
                () -> Assertions.assertNotNull(Objects.requireNonNull(response.getBody()).getRefreshToken()),
                () -> Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccessful())
        );
	}

    private static Stream<Arguments> loginTestDataWithENLocale() throws JsonProcessingException {
        // given
	    final String emptyEmail = prepareAuthJSONBody("", TEST_PASSWORD, ENABLE_ENCODING);
	    final String invalidEmailFormat = prepareAuthJSONBody("invalid-email-format", TEST_PASSWORD, ENABLE_ENCODING);
	    final String emptyPassword = prepareAuthJSONBody(TEST_EMAIL, "", ENABLE_ENCODING);
	    final String noCredentialsProvided = prepareAuthJSONBody("", "", ENABLE_ENCODING);

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, 2, new String[] {"Email address cannot be empty.", "Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, 1, new String[] {"Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, emptyPassword, 1, new String[] {"Password cannot be empty."}),
                Arguments.arguments(Method.POST, noCredentialsProvided, 3,
                                    new String[] {"Email address cannot be empty.", "Password cannot be empty.", "Provided email has incorrect format."})
        );
    }

	@DisplayName("Login failed. 400 Bad Request. \"Language=en\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginTestDataWithENLocale")
	void testLoginFailedWithENLocale(final Method httpMethod, final String jsonBody, final int errorsSize, final String... errorItems) {
		given().log().all()
               .header("Language", "en")
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, LOGIN_ENDPOINT)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body("message", equalTo("Cannot login user. Validation errors in login form."))
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems));
	}

    private static Stream<Arguments> loginTestDataWithPLLocale() throws JsonProcessingException {
        // given
        final String emptyEmail = prepareAuthJSONBody("", TEST_PASSWORD, ENABLE_ENCODING);
        final String invalidEmailFormat = prepareAuthJSONBody("invalid-email-format", TEST_PASSWORD, ENABLE_ENCODING);
        final String emptyPassword = prepareAuthJSONBody(TEST_EMAIL, "", ENABLE_ENCODING);
        final String noCredentialsProvided = prepareAuthJSONBody("", "", ENABLE_ENCODING);

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, 2, new String[] {"Adres email nie może być pusty.", "Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, 1, new String[] {"Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, emptyPassword, 1, new String[] {"Hasło nie może być puste."}),
                Arguments.arguments(Method.POST, noCredentialsProvided, 3,
                                    new String[] {"Adres email nie może być pusty.", "Hasło nie może być puste.", "Wprowadzony email ma nieprawidłowy format."})
        );
    }

    @DisplayName("Login failed. 400 Bad Request. \"Language=pl\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginTestDataWithPLLocale")
    void testLoginFailedWithPLLocale(final Method httpMethod, final String jsonBody, final int errorsSize, final String... errorItems) {
        given().log().all()
               .header("Language", "pl")
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, LOGIN_ENDPOINT)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body("message", equalTo("Nie można zalogować użytkownika. Formularz logowania zawiera błędy."))
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems));
    }
}
