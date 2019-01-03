package me.grudzien.patryk.unit.controller.jwt;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.stream.Stream;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.controller.jwt.JwtTokenController;
import me.grudzien.patryk.service.jwt.JwtTokenService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = JwtTokenController.class, secure = false)
class JwtTokenControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private JwtTokenService jwtTokenService;

    private static final boolean DISABLE_ENCODING = false;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    private static Stream<Arguments> testArguments() throws JsonProcessingException {
        return Stream.of(
                Arguments.arguments(RandomStringUtils.randomAlphanumeric(25),
                                    TestsUtils.prepareAuthJSONBody("test@email.com", "password", DISABLE_ENCODING),
                                    HttpStatus.OK),
                Arguments.arguments(null,
                                    TestsUtils.prepareAuthJSONBody("", "password", DISABLE_ENCODING),
                                    HttpStatus.BAD_REQUEST),
                Arguments.arguments(null,
                                    TestsUtils.prepareAuthJSONBody("test@email.com", "", DISABLE_ENCODING),
                                    HttpStatus.BAD_REQUEST),
                Arguments.arguments(null,
                                    TestsUtils.prepareAuthJSONBody("", "", DISABLE_ENCODING),
                                    HttpStatus.BAD_REQUEST),
                Arguments.arguments(null,
                                    TestsUtils.prepareAuthJSONBody(null, null, DISABLE_ENCODING),
                                    HttpStatus.BAD_REQUEST)
        );
    }

    @ParameterizedTest(name = "generateAccessToken. mockResponse = {0}. HttpStatus = {2}.")
    @MethodSource("testArguments")
    void generateAccessToken(final String mockResponse, final String jsonBody, final HttpStatus httpStatus) {
        // when
        when(jwtTokenService.generateAccessToken(any(), any())).thenReturn(mockResponse);

        // then
        RestAssuredMockMvc.given()
                          .body(jsonBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/generate-access-token")
                          .then()
                          .statusCode(httpStatus.value());

        verify(jwtTokenService, times(1)).generateAccessToken(any(), any());
    }

    @ParameterizedTest(name = "generateRefreshToken. mockResponse = {0}. HttpStatus = {2}.")
    @MethodSource("testArguments")
    void generateRefreshToken(final String mockResponse, final String jsonBody, final HttpStatus httpStatus) {
        // when
        when(jwtTokenService.generateRefreshToken(any(), any())).thenReturn(mockResponse);

        // then
        RestAssuredMockMvc.given()
                          .body(jsonBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/generate-refresh-token")
                          .then()
                          .statusCode(httpStatus.value());

        verify(jwtTokenService, times(1)).generateRefreshToken(any(), any());
    }

    @ParameterizedTest(name = "refreshAccessToken. mockResponse = {0}. HttpStatus = {2}.")
    @MethodSource("testArguments")
    void refreshAccessToken(final String mockResponse, final String jsonBody, final HttpStatus httpStatus) {
        // when
        when(jwtTokenService.refreshAccessToken(any(), any())).thenReturn(mockResponse);

        // then
        RestAssuredMockMvc.given()
                          .body(jsonBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/refresh-access-token")
                          .then()
                          .statusCode(httpStatus.value());

        verify(jwtTokenService, times(1)).refreshAccessToken(any(), any());
    }
}
