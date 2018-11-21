package me.grudzien.patryk.integration.login;

import io.jsonwebtoken.lang.Assert;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Base64;
import java.util.Set;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.util.validator.CustomValidator;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserAuthenticationControllerIntegrationTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private CustomApplicationProperties customApplicationProperties;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Validator validator;
	private String loginEndpoint;

	@Value("${server.port}")
	private int serverPort;

	@BeforeEach
	void setUp() {
		validator = CustomValidator.getDefaultValidator();

		RestAssured.port = serverPort;
		RestAssured.baseURI = "http://localhost";

		loginEndpoint = customApplicationProperties.getEndpoints().getApiContextPath() +
		                customApplicationProperties.getEndpoints().getAuthentication().getRoot();
	}

	@Test
	@DisplayName("Login successful. 200 OK.")
	void login_successful_responseStatusOk() {
		final String password = Base64.getEncoder().encodeToString("admin".getBytes());
		// arrange
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder()
		                                                                      .email("admin.root@gmail.com")
		                                                                      .password(password).build();
		// act
		final ResponseEntity<JwtAuthenticationResponse> response = testRestTemplate.postForEntity(loginEndpoint, loginRequest, JwtAuthenticationResponse.class);

		// assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
	}

	/**
	 * Test written using REST Assured.
	 */
	@Test
	@DisplayName("Login failed. Validation errors - empty password. Body message written using \"en\" locale. 400 Bad Request.")
	void login_failed_emptyPassword_validationErrors_enBodyMessages_badRequest() throws JsonProcessingException {
		// arrange
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder()
		                                                                      .email("admin.root@gmail.com")
		                                                                      .password("")
		                                                                      .refreshToken("test_refresh_token").build();
		final String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

		// act
		final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidationConstraints = validator.validate(loginRequest);

		// assert
		Assert.notEmpty(loginValidationConstraints);

		given()
				// log request body
				.log().body()
				.header("Language", "en")
				// payload
				.with().body(jsonLoginRequest)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				// http method
				.request(Method.POST, loginEndpoint)
				.then()
				// log response body
				.log().body()
				.assertThat()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.body("message", equalTo("Cannot login user. Validation errors in login form."))
				// errors is an JSON array
				.body("errors", hasItems("Password cannot be empty."));
	}

	@Test
	@DisplayName("Login failed. Validation errors - empty password. Body message written using \"pl\" locale. 400 Bad Request.")
	void login_failed_emptyPassword_validationErrors_plBodyMessages_badRequest() throws JsonProcessingException {
		// arrange
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder()
		                                                                      .email("admin.root@gmail.com")
		                                                                      .password("")
		                                                                      .refreshToken("test_refresh_token").build();
		final String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

		// act
		final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidationConstraints = validator.validate(loginRequest);

		// assert
		Assert.notEmpty(loginValidationConstraints);

		given().log().body()
		       .header("Language", "pl")
		       .with().body(jsonLoginRequest)
		       .contentType(ContentType.JSON)
		       .accept(ContentType.JSON)
		       .when()
		       .request(Method.POST, loginEndpoint)
		       .then()
		       .log().body()
		       .assertThat()
		       .statusCode(HttpStatus.BAD_REQUEST.value())
		       .body("message", equalTo("Nie można zalogować użytkownika. Formularz logowania zawiera błędy."))
		       .body("errors", hasItems("Hasło nie może być puste."));
	}
}
