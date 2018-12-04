package me.grudzien.patryk.unit.controller.security;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.controller.security.JwtTokenController;
import me.grudzien.patryk.service.security.JwtTokenService;

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

    @Test
    void testGenerateAccessToken() throws JsonProcessingException {
        // given
        final String requestBody = TestsUtils.prepareAuthJSONBody("test@email.com", "password", DISABLE_ENCODING);

        // when
        when(jwtTokenService.generateAccessToken(any(), any())).thenReturn(RandomStringUtils.randomAlphanumeric(25));

        // then
        RestAssuredMockMvc.given()
                          .body(requestBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/generate-access-token")
                          .then()
                          .statusCode(HttpStatus.OK.value());

        verify(jwtTokenService, times(1)).generateAccessToken(any(), any());
    }

    @Test
    void testGenerateRefreshToken() throws JsonProcessingException {
        // given
        final String requestBody = TestsUtils.prepareAuthJSONBody("test@email.com", "password", DISABLE_ENCODING);

        // when
        when(jwtTokenService.generateRefreshToken(any(), any())).thenReturn(RandomStringUtils.randomAlphanumeric(25));

        // then
        RestAssuredMockMvc.given()
                          .body(requestBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/generate-refresh-token")
                          .then()
                          .statusCode(HttpStatus.OK.value());

        verify(jwtTokenService, times(1)).generateRefreshToken(any(), any());
    }

    @Test
    void testRefreshAccessToken() throws JsonProcessingException {
        // given
        final String requestBody = TestsUtils.prepareAuthJSONBody("test@email.com", "password", DISABLE_ENCODING);

        // when
        when(jwtTokenService.refreshAccessToken(any(), any())).thenReturn(RandomStringUtils.randomAlphanumeric(25));

        // then
        RestAssuredMockMvc.given()
                          .body(requestBody)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post("/api/token/refresh-access-token")
                          .then()
                          .statusCode(HttpStatus.OK.value());

        verify(jwtTokenService, times(1)).refreshAccessToken(any(), any());
    }
}
