package me.grudzien.patryk.integration.configuration.security;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.stream.Stream;

import me.grudzien.patryk.jwt.service.JwtTokenService;
import me.grudzien.patryk.jwt.utils.JwtTokenConstants;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.params.provider.Arguments.*;

import static me.grudzien.patryk.TestsUtils.DISABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.ENABLE_ENCODING;
import static me.grudzien.patryk.TestsUtils.TEST_EMAIL;
import static me.grudzien.patryk.TestsUtils.TEST_PASSWORD;
import static me.grudzien.patryk.TestsUtils.prepareAccessTokenRequest;
import static me.grudzien.patryk.TestsUtils.prepareAuthJSONBody;
import static me.grudzien.patryk.TestsUtils.prepareJwtTokenControllerJSONBody;
import static me.grudzien.patryk.TestsUtils.prepareRegistrationJSONBody;
import static me.grudzien.patryk.TestsUtils.prepareVehicleJSONBody;
import static me.grudzien.patryk.TestsUtils.testDevice;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.AUTHENTICATION_LOGIN_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.AUTHENTICATION_PRINCIPAL_USER_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.JWT_GENERATE_ACCESS_TOKEN_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.JWT_GENERATE_REFRESH_TOKEN_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.JWT_REFRESH_ACCESS_TOKEN_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.REGISTRATION_CREATE_USER_ACCOUNT_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.VEHICLE_GET_BY_OWNER_EMAIL_ADDRESS_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.VEHICLE_GET_CURRENT_MILEAGE_URI;
import static me.grudzien.patryk.integration.configuration.security.BaseSecurityConfig.VEHICLE_UPDATE_CURRENT_MILEAGE_URI;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SecurityConfigIT {

    @LocalServerPort
	private int localServerPort;

    @Autowired
    private JwtTokenService jwtTokenService;

    @BeforeEach
	void setUp() {
		RestAssured.port = localServerPort;
		RestAssured.baseURI = "http://localhost";
	}

    private static Stream<Arguments> securedResourcesTestData() throws JsonProcessingException {
        final String vehicleJSONBody = prepareVehicleJSONBody("123456");
        return Stream.of(
                arguments(Method.GET, "", AUTHENTICATION_PRINCIPAL_USER_URI),
                arguments(Method.GET, "", VEHICLE_GET_BY_OWNER_EMAIL_ADDRESS_URI),
                arguments(Method.GET, "", VEHICLE_GET_CURRENT_MILEAGE_URI),
                arguments(Method.PUT, vehicleJSONBody, VEHICLE_UPDATE_CURRENT_MILEAGE_URI)
        );
    }

	@DisplayName("Testing secured endpoints with \"Language=pl\" header:")
	@ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). Authentication required! 401 Unauthorized.")
	@MethodSource("securedResourcesTestData")
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
    @MethodSource("securedResourcesTestData")
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

    @DisplayName("Testing secured endpoints with (correctly) constructed (but expired) JWT token:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 401 Unauthorized.")
    @MethodSource("securedResourcesTestData")
    void testSecuredEndpoints_withCorrectlyConstructedButExpiredToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
        given().log().all()
               .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenConstants.BEARER +
                       jwtTokenService.generateAccessTokenCustomExpiration(prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING), testDevice(), 0L))
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
               .body("securityStatus", hasEntry("securityStatusDescription", "JWT Token Expired!"))
               .body("lastRequestedPath", equalTo(permittedEndpoint.replaceAll("@", "%40")))  // replacing "@" as HttpServletRequest encode this char
               .body("lastRequestMethod", equalTo(httpMethod.name()));
    }

    @DisplayName("Testing secured endpoints with (correctly) constructed JWT token:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}).")
    @MethodSource("securedResourcesTestData")
    void testSecuredEndpoints_withCorrectlyConstructedToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
        final String s = UriComponentsBuilder.newInstance().path("/resend-verification-token").query("token=").toUriString();
        given().log().all()
               .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenConstants.BEARER +
                       jwtTokenService.generateAccessToken(prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING), testDevice()))
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, permittedEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(Matchers.not(HttpStatus.UNAUTHORIZED.value()));
    }

    private static Stream<Arguments> permittedResourcesTestData() throws JsonProcessingException {
        final String authJSONBody = prepareAuthJSONBody(TEST_EMAIL, TEST_PASSWORD, ENABLE_ENCODING);
        final String jwtTokenControllerJSONBody = prepareJwtTokenControllerJSONBody(TEST_EMAIL, TEST_PASSWORD, DISABLE_ENCODING);
        final String registrationJSONBody = prepareRegistrationJSONBody("John", "Snow", "test@email.com",
                                                                                   "password", ENABLE_ENCODING);
        return Stream.of(
                arguments(Method.POST, authJSONBody, AUTHENTICATION_LOGIN_URI),
                arguments(Method.POST, registrationJSONBody, REGISTRATION_CREATE_USER_ACCOUNT_URI),
                arguments(Method.POST, jwtTokenControllerJSONBody, JWT_GENERATE_ACCESS_TOKEN_URI),
                arguments(Method.POST, jwtTokenControllerJSONBody, JWT_GENERATE_REFRESH_TOKEN_URI),
                arguments(Method.POST, jwtTokenControllerJSONBody, JWT_REFRESH_ACCESS_TOKEN_URI)
        );
    }

	@DisplayName("Testing permitted endpoints with (incorrectly) constructed JWT token:")
	@ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 406 Not Acceptable.")
	@MethodSource("permittedResourcesTestData")
	void testPermittedEndpoints_withIncorrectlyConstructedToken(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
		given().log().all()
		       .with().body(jsonBody)
               .header("Language", "en")
               .header("Authorization", JwtTokenConstants.BEARER + RandomStringUtils.randomAlphanumeric(20))
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
               .header("Authorization", JwtTokenConstants.BEARER +
                       jwtTokenService.generateAccessToken(prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING), testDevice()))
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
               .header("Authorization", JwtTokenConstants.BEARER +
                       jwtTokenService.generateAccessTokenCustomExpiration(prepareAccessTokenRequest(TEST_EMAIL, ENABLE_ENCODING), testDevice(), 0L))
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

    @DisplayName("Testing permitted endpoints without \"Authorization\" header (JWT token):")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 200 OK.")
    @MethodSource("permittedResourcesTestData")
    void testPermittedEndpoints_withoutAuthorizationHeader(final Method httpMethod, final String jsonBody, final String permittedEndpoint) throws JsonProcessingException {
        // given
        final String anotherRegistrationJSONBody = prepareRegistrationJSONBody("John", "Snow", "test-2@email.com",
                                                                                          "password-2", ENABLE_ENCODING);
        // when, then
        given().log().all()
               .with().body(permittedEndpoint.equals(REGISTRATION_CREATE_USER_ACCOUNT_URI) ? anotherRegistrationJSONBody : jsonBody)
               .header("Language", "en")
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, permittedEndpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(HttpStatus.OK.value()).and();
    }
}
