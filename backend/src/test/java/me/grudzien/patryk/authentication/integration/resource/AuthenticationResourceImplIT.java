package me.grudzien.patryk.authentication.integration.resource;

import io.restassured.RestAssured;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.POST;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
class AuthenticationResourceImplIT extends BaseAuthenticationResourceIT {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int localServerPort;

	@BeforeEach
	void setUp() {
		RestAssured.port = localServerPort;
		RestAssured.baseURI = HTTP_LOCALHOST;
	}

	@Test
	@DisplayName("Login successful. Response status: 200 OK.")
	void shouldReturn200withAccessAndRefreshTokensOnLogin() {
        // given
		final JwtAuthenticationRequest loginRequest = createLoginRequest().doEncoding(false);

		// when
		final ResponseEntity<JwtAuthenticationResponse> response =
                testRestTemplate.postForEntity(AUTHENTICATION_LOGIN_URI, loginRequest, JwtAuthenticationResponse.class);

		// then
        final JwtAuthenticationResponse responseBody = response.getBody();
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(OK),
                () -> assertNotNull(responseBody),
                () -> assertThat(requireNonNull(responseBody).getAccessToken()).isNotNull(),
                () -> assertThat(requireNonNull(responseBody).getRefreshToken()).isNotNull(),
                () -> assertThat(requireNonNull(responseBody).isSuccessful()).isTrue()
        );
	}

    private static Stream<Arguments> loginMethodSourceLanguageEN() {
        final String emptyEmail = createLoginJsonRequest("", TEST_PASSWORD).doEncoding(true);
	    final String invalidEmailFormat = createLoginJsonRequest("invalid-email-format", TEST_PASSWORD).doEncoding(true);
	    final String emptyPassword = createLoginJsonRequest(TEST_EMAIL, "").doEncoding(true);
	    final String noCredentialsProvided = createLoginJsonRequest("", "").doEncoding(true);

        return Stream.of(
                arguments(POST, emptyEmail, 2,
                          new String[] {"Email address cannot be empty.", "Provided email has incorrect format."}),
                arguments(POST, invalidEmailFormat, 1,
                          new String[] {"Provided email has incorrect format."}),
                arguments(POST, emptyPassword, 1,
                          new String[] {"Password cannot be empty."}),
                arguments(POST, noCredentialsProvided, 3,
                          new String[] {"Email address cannot be empty.", "Password cannot be empty.", "Provided email has incorrect format."})
        );
    }

	@DisplayName("Login failed. 400 Bad Request. \"Language=en\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginMethodSourceLanguageEN")
	void shouldReturn400onLoginWhenFormContainsValidationErrorsLanguageEN(final Method httpMethod, final String jsonBody,
                                                                          final int errorsSize, final String... errorItems) {
		given().log().all()
               .header("Language", "en")
               .with().body(jsonBody)
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(httpMethod, AUTHENTICATION_LOGIN_URI)
               .then()
               .log().body()
               .assertThat()
               .statusCode(BAD_REQUEST.value())
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems))
               .body("messageCode", equalTo("login-form-validation-errors"));
	}

    private static Stream<Arguments> loginMethodSourceLanguagePL() {
        final String emptyEmail = createLoginJsonRequest("", TEST_PASSWORD).doEncoding(true);
        final String invalidEmailFormat = createLoginJsonRequest("invalid-email-format", TEST_PASSWORD).doEncoding(true);
        final String emptyPassword = createLoginJsonRequest(TEST_EMAIL, "").doEncoding(true);
        final String noCredentialsProvided = createLoginJsonRequest("", "").doEncoding(true);

        return Stream.of(
                arguments(POST, emptyEmail, 2,
                          new String[] {"Adres email nie może być pusty.", "Wprowadzony email ma nieprawidłowy format."}),
                arguments(POST, invalidEmailFormat, 1,
                          new String[] {"Wprowadzony email ma nieprawidłowy format."}),
                arguments(POST, emptyPassword, 1,
                          new String[] {"Hasło nie może być puste."}),
                arguments(POST, noCredentialsProvided, 3,
                          new String[] {"Adres email nie może być pusty.", "Hasło nie może być puste.", "Wprowadzony email ma nieprawidłowy format."})
        );
    }

    @DisplayName("Login failed. 400 Bad Request. \"Language=pl\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginMethodSourceLanguagePL")
    void shouldReturn400onLoginWhenFormContainsValidationErrorsLanguagePL(final Method httpMethod, final String jsonBody,
                                                                          final int errorsSize, final String... errorItems) {
        given().log().all()
               .header("Language", "pl")
               .with().body(jsonBody)
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(httpMethod, AUTHENTICATION_LOGIN_URI)
               .then()
               .log().body()
               .assertThat()
               .statusCode(BAD_REQUEST.value())
               .body("errors", hasSize(errorsSize))
               .body("errors", hasItems(errorItems))
               .body("messageCode", equalTo("login-form-validation-errors"));
    }
}
