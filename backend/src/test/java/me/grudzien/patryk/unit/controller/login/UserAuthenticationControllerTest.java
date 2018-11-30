package me.grudzien.patryk.unit.controller.login;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import me.grudzien.patryk.controller.login.UserAuthenticationController;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.util.validator.CustomValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAuthenticationController.class, secure = false)
class UserAuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

	@MockBean
	private UserAuthenticationService userAuthenticationService;

    private final Validator validator = CustomValidator.getDefaultValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password";
    private static final String LOGIN_ENDPOINT = "/api/auth";

	@Test
    @DisplayName("Login successful. Response status -> 200 OK.")
	void testLoginSuccessful() throws Exception {
	    // given
		final JwtAuthenticationResponse expectedResponse = JwtAuthenticationResponse.Builder()
		                                                                            .accessToken(RandomStringUtils.randomAlphanumeric(25))
		                                                                            .refreshToken(RandomStringUtils.randomAlphanumeric(25))
		                                                                            .isSuccessful(Boolean.TRUE)
		                                                                            .build();
		// when
		when(userAuthenticationService.login(any(), any())).thenReturn(expectedResponse);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN_ENDPOINT)
		                                                            .accept(MediaType.APPLICATION_JSON)
		                                                            .content(prepareAuthJSONRequest(TEST_EMAIL, TEST_PASSWORD))
		                                                            .contentType(MediaType.APPLICATION_JSON);
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isOk())
		                                   .andReturn();
		// then
        final JwtAuthenticationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);

		verify(userAuthenticationService, times(1)).login(any(), any());
        Assertions.assertAll(
                () -> assertThat(actualResponse.getAccessToken()).hasSize(25),
                () -> assertThat(actualResponse.getRefreshToken()).hasSize(25),
                () -> assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken()),
                () -> assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken()),
                () -> assertTrue(actualResponse.isSuccessful()),
                () -> assertEquals(expectedResponse.isSuccessful(), actualResponse.isSuccessful())
        );
	}

	@Test
    @DisplayName("Login failed. Empty email! Response status -> 400 Bad Request.")
	void testLoginFailed_emptyEmail() throws Exception {
        // given
		final String jsonLoginRequest = prepareAuthJSONRequest("", TEST_PASSWORD);

		// when
		when(userAuthenticationService.login(any(), any())).thenReturn(new JwtAuthenticationResponse());
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN_ENDPOINT)
		                                                            .accept(MediaType.APPLICATION_JSON)
		                                                            .content(jsonLoginRequest)
		                                                            .contentType(MediaType.APPLICATION_JSON);
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isBadRequest())
		                                   .andReturn();
		// then
        final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidationResult = 
                validator.validate(objectMapper.readValue(jsonLoginRequest, JwtAuthenticationRequest.class));
        final JwtAuthenticationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                                                                                new TypeReference<JwtAuthenticationResponse>() {});

        verify(userAuthenticationService, times(1)).login(any(), any());
		Assertions.assertAll(
                () -> Assertions.assertNotNull(loginValidationResult),
                () -> assertThat(loginValidationResult).hasSize(2),
                () -> Assertions.assertFalse(actualResponse.isSuccessful())
        );
    }

    @Test
    @DisplayName("Login failed. Empty password! Response status -> 400 Bad Request.")
    void testLoginFailed_emptyPassword() throws IOException {
	    // given
        final String jsonLoginRequest = prepareAuthJSONRequest(TEST_EMAIL, "");

        // when
        when(userAuthenticationService.login(any(), any())).thenReturn(new JwtAuthenticationResponse());

        // then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().all()
                          .body(jsonLoginRequest)
                          .contentType(ContentType.JSON)
                          .accept(ContentType.JSON)
                          .when()
                          .post(LOGIN_ENDPOINT)
                          .then()
                          .log().all()
                          .statusCode(HttpStatus.BAD_REQUEST.value())
                          .body("successful", Matchers.is(false));

        final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidationResult =
                validator.validate(objectMapper.readValue(jsonLoginRequest, JwtAuthenticationRequest.class));

        verify(userAuthenticationService, times(1)).login(any(), any());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(loginValidationResult),
                () -> assertThat(loginValidationResult).hasSize(1)
        );
    }
    
    @SuppressWarnings("Duplicates")
    private String prepareAuthJSONRequest(final String plainEmail, final String plainPassword) throws JsonProcessingException {
        final String email = Optional.ofNullable(plainEmail).orElse(null);
        final String password = Optional.ofNullable(plainPassword).orElse(null);

        return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(email)
                                                                       .password(password)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }
}
