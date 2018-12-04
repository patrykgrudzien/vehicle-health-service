package me.grudzien.patryk.integration.security;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.stream.Stream;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.util.jwt.JwtTokenUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SecurityConfigIT {

    @LocalServerPort
	private int localServerPort;

    private static final String TEST_EMAIL = "test@email.com";
    private static final String PASSWORD = "password";

	@BeforeEach
	void setUp() {
		RestAssured.port = localServerPort;
		RestAssured.baseURI = "http://localhost";
	}

    private static Stream<Arguments> protectedResourcesTestData() throws JsonProcessingException {
        final String vehicleJSONBody = TestsUtils.prepareVehicleJSONBody("123456");
        return Stream.of(
                Arguments.arguments(Method.GET, "", "/api/principal-user"),
                Arguments.arguments(Method.GET, "", "/api/vehicles/vehicle/test@email.com"),
                Arguments.arguments(Method.GET, "", "/api/vehicles/vehicle/get-current-mileage/test@email.com"),
                Arguments.arguments(Method.PUT, vehicleJSONBody, "/api/vehicles/vehicle/update-current-mileage/test@email.com")
        );
    }

	@DisplayName("Testing secured endpoints with \"Language=pl\" header:")
	@ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). Authentication required! 401 Unauthorized.")
	@MethodSource("protectedResourcesTestData")
	void testProtectedEndpointsWithPLLocale(final Method httpMethod, final String jsonBody, final String protectedEndpoint) {
		given().log().all()
		       .with().body(jsonBody)
               .header("Language", "pl")
		       .contentType(ContentType.JSON)
		       .accept(ContentType.JSON)
		       .when()
		       .request(httpMethod, protectedEndpoint)
		       .then()
		       .log().body()
		       .assertThat()
		       .statusCode(HttpStatus.UNAUTHORIZED.value())
		       .body("message", equalTo("Nie masz uprawnień, żeby sprawdzić zabezpieczony zasób!"))
		       .body("securityStatus", hasEntry("securityStatusCode", "UNAUTHENTICATED"))
		       .body("securityStatus", hasEntry("securityStatusDescription", "Unauthenticated! (access_token) has NOT been provided with the request!"));
	}

    @DisplayName("Testing secured endpoints with \"Language=en\" header:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). Authentication required! 401 Unauthorized.")
    @MethodSource("protectedResourcesTestData")
    void testProtectedEndpointsWithENLocale(final Method httpMethod, final String jsonBody, final String protectedEndpoint) {
        given().log().all()
               .with().body(jsonBody)
               .header("Language", "en")
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, protectedEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.UNAUTHORIZED.value())
               .body("message", equalTo("You must be authenticated to enter secured resource!"))
               .body("securityStatus", hasEntry("securityStatusCode", "UNAUTHENTICATED"))
               .body("securityStatus", hasEntry("securityStatusDescription", "Unauthenticated! (access_token) has NOT been provided with the request!"));
    }

    private static Stream<Arguments> permittedResourcesTestData() throws JsonProcessingException {
        // -> /api/auth
        final String authJSONBody = TestsUtils.prepareAuthJSONBody(TEST_EMAIL, PASSWORD, true);
        // -> /api/refresh-token
        final String refreshAccessTokenJSONBody = TestsUtils.prepareRefreshAccessTokenJSONBody();
        // -> /api/registration/register-user-account
        final String registrationJSONBody = TestsUtils.prepareRegistrationJSONBody("John", "Snow", TEST_EMAIL, PASSWORD, true);

        return Stream.of(
                Arguments.arguments(Method.POST, authJSONBody, "/api/auth"),
                Arguments.arguments(Method.POST, registrationJSONBody, "/api/registration/register-user-account"),
                Arguments.arguments(Method.POST, refreshAccessTokenJSONBody, "/api/token/refresh-access-token")
        );
    }

	@DisplayName("Testing permitted endpoints with (incorrectly) constructed JWT token:")
	@ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 406 Not Acceptable.")
	@MethodSource("permittedResourcesTestData")
	void testPermittedEndpoints_withIncorrectlyConstructedToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
		given().log().all()
		       .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenUtil.BEARER + RandomStringUtils.randomAlphanumeric(20))
		       .contentType(ContentType.JSON)
		       .accept(ContentType.JSON)
		       .when()
		       .request(httpMethod, permittedEndpoint)
		       .then()
		       .log().body()
		       .assertThat()
		       .statusCode(HttpStatus.NOT_ACCEPTABLE.value())
               .and()
               .body("securityStatus", hasEntry("securityStatusCode", "JWT_TOKEN_NOT_CORRECTLY_CONSTRUCTED"))
               .body("securityStatus", hasEntry("securityStatusDescription", "JWT token has NOT been correctly constructed!"));
	}

    @DisplayName("Testing permitted endpoints with (correctly) constructed JWT token:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 200 OK.")
    @MethodSource("permittedResourcesTestData")
    void testPermittedEndpoints_withCorrectlyConstructedToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
        given().log().all()
               .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenUtil.BEARER + JwtTokenUtil.Creator.generateAccessToken(TestsUtils.prepareTestJwtUser(), TestsUtils.testDevice()))
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, permittedEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("Testing permitted endpoints with (correctly) constructed (but expired) JWT token:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 401 Unauthorized.")
    @MethodSource("permittedResourcesTestData")
    void testPermittedEndpoints_withCorrectlyConstructedButExpiredToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
        given().log().all()
               .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenUtil.BEARER +
                       JwtTokenUtil.Creator.generateAccessToken(TestsUtils.prepareTestJwtUser(), new Date(), TestsUtils.testDevice()))
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, permittedEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.UNAUTHORIZED.value())
               .and()
               .body("securityStatus", hasEntry("securityStatusCode", "JWT_TOKEN_EXPIRED"))
               .body("securityStatus", hasEntry("securityStatusDescription", "JWT Token Expired!"));
    }
}
