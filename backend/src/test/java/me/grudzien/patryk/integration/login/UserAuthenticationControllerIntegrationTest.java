package me.grudzien.patryk.integration.login;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserAuthenticationControllerIntegrationTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Value("${server.port}")
	private int serverPort;

	@BeforeEach
	void setUp() {
		RestAssured.port = serverPort;
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
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity("/api/auth", loginRequest, JwtAuthenticationResponse.class);

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
	    final String loginEndpoint = "/api/auth";

	    final String emptyEmail = prepareAuthJSONRequest("", "admin");
	    final String invalidEmailFormat = prepareAuthJSONRequest("invalid-email-format", "admin");
	    final String emptyPassword = prepareAuthJSONRequest("admin.root@gmail.com", "");
	    final String noCredentialsProvided = prepareAuthJSONRequest("", "");

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, loginEndpoint,
                                    new String[] {"Email address cannot be empty.", "Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, loginEndpoint,
                                    new String[] {"Provided email has incorrect format."}),
                Arguments.arguments(Method.POST, emptyPassword, loginEndpoint,
                                    new String[] {"Password cannot be empty."}),
                Arguments.arguments(Method.POST, noCredentialsProvided, loginEndpoint,
                                    new String[] {"Email address cannot be empty.", "Password cannot be empty.", "Provided email has incorrect format."})
        );
    }

	@DisplayName("Login failed. 400 Bad Request. \"Language=en\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginTestDataWithENLocale")
	void testLoginFailedWithENLocale(final Method httpMethod, final String jsonBody, final String loginEndpoint, final String... errorItems) {
		given().log().all()
               .header("Language", "en")
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, loginEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body("message", equalTo("Cannot login user. Validation errors in login form."))
               .body("errors", hasItems(errorItems));
	}

    private static Stream<Arguments> loginTestDataWithPLLocale() throws JsonProcessingException {
        // arrange
        final String loginEndpoint = "/api/auth";

        final String emptyEmail = prepareAuthJSONRequest("", "admin");
        final String invalidEmailFormat = prepareAuthJSONRequest("invalid-email-format", "admin");
        final String emptyPassword = prepareAuthJSONRequest("admin.root@gmail.com", "");
        final String noCredentialsProvided = prepareAuthJSONRequest("", "");

        return Stream.of(
                Arguments.arguments(Method.POST, emptyEmail, loginEndpoint,
                                    new String[] {"Adres email nie może być pusty.", "Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, invalidEmailFormat, loginEndpoint,
                                    new String[] {"Wprowadzony email ma nieprawidłowy format."}),
                Arguments.arguments(Method.POST, emptyPassword, loginEndpoint,
                                    new String[] {"Hasło nie może być puste."}),
                Arguments.arguments(Method.POST, noCredentialsProvided, loginEndpoint,
                                    new String[] {"Adres email nie może być pusty.", "Hasło nie może być puste.", "Wprowadzony email ma nieprawidłowy format."})
        );
    }

    @DisplayName("Login failed. 400 Bad Request. \"Language=pl\" header. Validation errors: ")
    @ParameterizedTest(name = "{3}")
    @MethodSource("loginTestDataWithPLLocale")
    void testLoginFailedWithPLLocale(final Method httpMethod, final String jsonBody, final String loginEndpoint, final String... errorItems) {
        given().log().all()
               .header("Language", "pl")
               .with().body(jsonBody)
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, loginEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.BAD_REQUEST.value())
               .body("message", equalTo("Nie można zalogować użytkownika. Formularz logowania zawiera błędy."))
               .body("errors", hasItems(errorItems));
    }

    private static String prepareAuthJSONRequest(final String email, final String password) throws JsonProcessingException {
        final Encoder encoder = Base64.getEncoder();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String encodedEmail = Optional.ofNullable(email).map(notEmptyEmail -> encoder.encodeToString(notEmptyEmail.getBytes())).orElse(null);
        final String encodedPassword = Optional.ofNullable(password).map(notEmptyPassword -> encoder.encodeToString(notEmptyPassword.getBytes())).orElse(null);

	    return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(encodedEmail)
                                                                       .password(encodedPassword)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }
}
