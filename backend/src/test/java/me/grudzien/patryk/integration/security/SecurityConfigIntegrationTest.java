package me.grudzien.patryk.integration.security;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.grudzien.patryk.PropertiesKeeper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SecurityConfigIntegrationTest {

	@Autowired
	private PropertiesKeeper propertiesKeeper;

	private String getPrincipalUserEndpoint;

	@Value("${server.port}")
	private int serverPort;

	@BeforeEach
	void setUp() {
		RestAssured.port = serverPort;
		RestAssured.baseURI = "http://localhost";

		getPrincipalUserEndpoint = propertiesKeeper.endpoints().API_CONTEXT_PATH +
		                           propertiesKeeper.endpoints().authentication().GET_PRINCIPAL_USER;
	}

	@Test
	@DisplayName("getPrincipalUser(). Secured resource! Authentication required! 401 Unauthorized.")
	void getPrincipalUser_authenticationRequired_unauthorizedResponseCode() {
		given().log().uri()
		       .with()
		       .contentType(ContentType.JSON)
		       .accept(ContentType.JSON)
		       .when()
		       .request(Method.GET, getPrincipalUserEndpoint)
		       .then()
		       .log().body()
		       .assertThat()
		       .statusCode(HttpStatus.UNAUTHORIZED.value())
		       .body("message", equalTo("You must be authenticated to enter secured resource!"));
	}
}
