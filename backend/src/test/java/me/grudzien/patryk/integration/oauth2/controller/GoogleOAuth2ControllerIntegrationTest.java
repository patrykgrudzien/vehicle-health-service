package me.grudzien.patryk.integration.oauth2.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GoogleOAuth2ControllerIntegrationTest {

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        RestAssured.baseURI = "http://localhost";
    }

    private static Stream<Arguments> googleOAuth2ControllerTestData() {
        return Stream.of(
                Arguments.arguments(Method.GET, "/api/user-not-found", HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(Method.GET, "/api/user-account-is-locked", HttpStatus.FORBIDDEN.value()),
                Arguments.arguments(Method.GET, "/api/user-is-disabled", HttpStatus.FORBIDDEN.value()),
                Arguments.arguments(Method.GET, "/api/user-account-is-expired", HttpStatus.FORBIDDEN.value()),
                Arguments.arguments(Method.GET, "/api/user-account-already-exists", HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(Method.GET, "/api/credentials-have-expired", HttpStatus.FORBIDDEN.value()),
                Arguments.arguments(Method.GET, "/api/jwt-token-not-found", HttpStatus.FORBIDDEN.value()),
                Arguments.arguments(Method.GET, "/api/registration-provider-mismatch", HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(Method.GET, "/api/bad-credentials", HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(Method.GET, "/api/exchange-short-lived-token", HttpStatus.OK.value()),
                Arguments.arguments(Method.GET, "/api/user-registered-using-google", HttpStatus.OK.value()),
                Arguments.arguments(Method.GET, "/api/failure-target-url", HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }

    @DisplayName("Testing responses' status of GoogleOAuth2Controller:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({1}). Expected response status code -> ({2})")
    @MethodSource("googleOAuth2ControllerTestData")
    void testGoogleOAuth2Controller(final Method httpMethod, final String endpoint, final int statusCode) {
        given().log().all()
               .with()
               .header("Language", "en")
               .contentType(ContentType.JSON)
               .accept(ContentType.JSON)
               .when()
               .request(httpMethod, endpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(statusCode);
    }
}