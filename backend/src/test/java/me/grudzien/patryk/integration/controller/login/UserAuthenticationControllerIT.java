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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Objects;
import java.util.stream.Stream;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserAuthenticationControllerIT {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int localServerPort;

	private static final String LOGIN_ENDPOINT = "/api/auth";

    private static final boolean ENABLE_ENCODING = true;

	@BeforeEach
	void setUp() {
		RestAssured.port = localServerPort;
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	@DisplayName("Login successful. 200 OK.")
	void testLoginSuccessful() {
        final Encoder encoder = Base64.getEncoder();
        // arrange
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder()
		                                                                      .email(encoder.encodeToString("admin.root@gmail.com".getBytes()))
		                                                                      .password(encoder.encodeToString("admin".getBytes()))
		                                                                      .build();
		// act
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity(LOGIN_ENDPOINT, loginRequest, JwtAuthenticationResponse.class);

		// assertions
		Assertions.assertAll(
                () -> Assertions.assertNotNull(response.getBody()),
                () -> Assertions.assertNotNull(Objects.requireNonNull(response.getBody()).getAccessToken()),
                () -> Assertions.assertNotNull(Objects.requireNonNull(response.getBody()).getRefreshToken()),
                () -> Assertions.assertTrue(Objects.requireNonNull(response.getBody()).isSuccessful())
        );
	}

    private static Stream<Arguments> loginTestDataWithENLocale() throws JsonProcessingException {
        // arrange
	    final String emptyEmail = TestsUtils.prepareAuthJSONRequest("", "admin", ENABLE_ENCODING);
	    final String invalidEmailFormat = TestsUtils.prepareAuthJSONRequest("invalid-email-format", "admin", ENABLE_ENCODING);
	    final String emptyPassword = TestsUtils.prepareAuthJSONRequest("admin.root@gmail.com", "", ENABLE_ENCODING);
	    final String noCredentialsProvided = TestsUtils.prepareAuthJSONRequest("", "", ENABLE_ENCODING);

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, new String[] {"Email address cannot be empty.", "Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, new String[] {"Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, emptyPassword, new String[] {"Password cannot be empty."}),
                Arguments.arguments(Method.POST, noCredentialsProvided,
                                    new String[] {"Email address cannot be empty.", "Password cannot be empty.", "Provided email has incorrect format."})
        );
    }

	@DisplayName("Login failed. 400 Bad Request. \"Language=en\" header. Validation errors: ")
    @ParameterizedTest(name = "{2}")
    @MethodSource("loginTestDataWithENLocale")
	void testLoginFailedWithENLocale(final Method httpMethod, final String jsonBody, final String... errorItems) {
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
               .body("errors", hasItems(errorItems));
	}

    private static Stream<Arguments> loginTestDataWithPLLocale() throws JsonProcessingException {
        // arrange
        final String emptyEmail = TestsUtils.prepareAuthJSONRequest("", "admin", ENABLE_ENCODING);
        final String invalidEmailFormat = TestsUtils.prepareAuthJSONRequest("invalid-email-format", "admin", ENABLE_ENCODING);
        final String emptyPassword = TestsUtils.prepareAuthJSONRequest("admin.root@gmail.com", "", ENABLE_ENCODING);
        final String noCredentialsProvided = TestsUtils.prepareAuthJSONRequest("", "", ENABLE_ENCODING);

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, new String[] {"Adres email nie może być pusty.", "Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, new String[] {"Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, emptyPassword, new String[] {"Hasło nie może być puste."}),
                Arguments.arguments(Method.POST, noCredentialsProvided,
                                    new String[] {"Adres email nie może być pusty.", "Hasło nie może być puste.", "Wprowadzony email ma nieprawidłowy format."})
        );
    }

    @DisplayName("Login failed. 400 Bad Request. \"Language=pl\" header. Validation errors: ")
    @ParameterizedTest(name = "{2}")
    @MethodSource("loginTestDataWithPLLocale")
    void testLoginFailedWithPLLocale(final Method httpMethod, final String jsonBody, final String... errorItems) {
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
               .body("errors", hasItems(errorItems));
    }
}
