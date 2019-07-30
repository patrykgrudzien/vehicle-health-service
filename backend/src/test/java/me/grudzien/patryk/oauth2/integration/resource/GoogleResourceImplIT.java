package me.grudzien.patryk.oauth2.integration.resource;

import io.restassured.RestAssured;
import io.restassured.http.Method;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class GoogleResourceImplIT extends BaseGoogleResource {

    @LocalServerPort
    private int localServerPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = localServerPort;
        RestAssured.baseURI = "http://localhost";
    }

    private static Stream<Arguments> googleOAuth2ControllerTestData() {
        return Stream.of(
                arguments(GET, GOOGLE_OAUTH2_USER_NOT_FOUND_URI, NOT_FOUND.value()),
                arguments(GET, GOOGLE_OAUTH2_USER_ACCOUNT_IS_LOCKED, FORBIDDEN.value()),
                arguments(GET, GOOGLE_OAUTH2_USER_IS_DISABLED, FORBIDDEN.value()),
                arguments(GET, GOOGLE_OAUTH2_USER_ACCOUNT_IS_EXPIRED, FORBIDDEN.value()),
                arguments(GET, GOOGLE_OAUTH2_USER_ACCOUNT_ALREADY_EXISTS, BAD_REQUEST.value()),
                arguments(GET, GOOGLE_OAUTH2_CREDENTIALS_HAVE_EXPIRED, FORBIDDEN.value()),
                arguments(GET, GOOGLE_OAUTH2_JWT_TOKEN_NOT_FOUND, FORBIDDEN.value()),
                arguments(GET, GOOGLE_OAUTH2_REGISTRATION_PROVIDER_MISMATCH, BAD_REQUEST.value()),
                arguments(GET, GOOGLE_OAUTH2_BAD_CREDENTIALS, BAD_REQUEST.value()),
                arguments(GET, GOOGLE_OAUTH2_EXCHANGE_SHORT_LIVED_TOKEN, OK.value()),
                arguments(GET, GOOGLE_OAUTH2_USER_REGISTERED_USING_GOOGLE, OK.value()),
                arguments(GET, GOOGLE_OAUTH2_FAILURE_TARGET_URL, INTERNAL_SERVER_ERROR.value())
        );
    }

    @DisplayName("Testing responses' status of GoogleOAuth2Controller:")
    @ParameterizedTest(name = "Method -> ({0}), URL -> ({1}). Expected response status code -> ({2})")
    @MethodSource("googleOAuth2ControllerTestData")
    void testGoogleOAuth2Controller(final Method httpMethod, final String endpoint, final int statusCode) {
        given().log().all()
               .with()
               .header("Language", "en")
               .contentType(JSON)
               .accept(JSON)
               .when()
               .request(httpMethod, endpoint)
               .then()
               .log().body()
               .assertThat()
               .statusCode(statusCode);
    }
}