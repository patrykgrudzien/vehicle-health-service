package me.grudzien.patryk.integration.security;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.stream.Stream;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.registration.UserRegistrationDto;
import me.grudzien.patryk.domain.dto.vehicle.VehicleDto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SecurityConfigIntegrationTest {

	@Value("${server.port}")
	private int serverPort;

	@BeforeEach
	void setUp() {
		RestAssured.port = serverPort;
		RestAssured.baseURI = "http://localhost";
	}

    private static Stream<Arguments> protectedResourcesTestData() throws JsonProcessingException {
        final VehicleDto putBody = VehicleDto.Builder().encodedMileage(Base64.getEncoder().encodeToString("123456".getBytes())).build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonBody = objectMapper.writeValueAsString(putBody);

        return Stream.of(
                Arguments.arguments(Method.GET, "", "/api/principal-user"),
                Arguments.arguments(Method.GET, "", "/api/vehicles/vehicle/test@email.com"),
                Arguments.arguments(Method.GET, "", "/api/vehicles/vehicle/get-current-mileage/test@email.com"),
                Arguments.arguments(Method.PUT, jsonBody, "/api/vehicles/vehicle/update-current-mileage/test@email.com")
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
        final Base64.Encoder encoder = Base64.getEncoder();
        // -> /api/auth
        final JwtAuthenticationRequest authBody = JwtAuthenticationRequest.Builder()
                                                                          .email(encoder.encodeToString("test@email.com".getBytes()))
                                                                          .password(encoder.encodeToString("password".getBytes()))
                                                                          .build();
        // -> /api/refresh-token
        final JwtAuthenticationRequest refreshBody = JwtAuthenticationRequest.Builder().refreshToken(RandomStringUtils.randomAlphanumeric(25)).build();

        // -> /api/registration/register-user-account
        final UserRegistrationDto registrationBody = UserRegistrationDto.Builder()
                                                                        .firstName("admin")
                                                                        .lastName("root")
                                                                        .email("test@email.com")
                                                                        .confirmedEmail("test@email.com")
                                                                        .hasFakeEmail(true)
                                                                        .password(encoder.encodeToString("password".getBytes()))
                                                                        .confirmedPassword(encoder.encodeToString("password".getBytes()))
                                                                        .build();
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonAuthBody = objectMapper.writeValueAsString(authBody);
        final String jsonRefreshBody = objectMapper.writeValueAsString(refreshBody);
        final String jsonRegistrationBody = objectMapper.writeValueAsString(registrationBody);

        return Stream.of(
                Arguments.arguments(Method.POST, jsonAuthBody, "/api/auth"),
                Arguments.arguments(Method.POST, jsonRefreshBody, "/api/refresh-token"),
                Arguments.arguments(Method.POST, jsonRegistrationBody, "/api/registration/register-user-account")
        );
    }

	@DisplayName("Testing permitted endpoints:")
	@ParameterizedTest(name = "Method -> ({0}), URL -> ({2}). 200 OK.")
	@MethodSource("permittedResourcesTestData")
	void testPermittedEndpoints(final Method httpMethod, final String jsonBody, final String permittedEndpoint) {
		given().log().all()
		       .with().body(jsonBody)
               .header("Language", "en")
		       .contentType(ContentType.JSON)
		       .accept(ContentType.JSON)
		       .when()
		       .request(httpMethod, permittedEndpoint)
		       .then()
		       .log().body()
		       .assertThat()
		       .statusCode(HttpStatus.OK.value());
	}
}
