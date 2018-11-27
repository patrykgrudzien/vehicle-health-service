package me.grudzien.patryk.unit.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;
import java.util.Set;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.controller.login.UserAuthenticationController;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.util.validator.CustomValidator;

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
	private CustomApplicationProperties customApplicationProperties;

	@MockBean
	private UserAuthenticationService userAuthenticationService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private Validator validator;
	private String loginEndpoint;

	@BeforeEach
	void setUp() {
		validator = CustomValidator.getDefaultValidator();
        loginEndpoint = customApplicationProperties.getEndpoints().getApiContextPath() +
                customApplicationProperties.getEndpoints().getAuthentication().getRoot();
	}

	@Test
    @DisplayName("Login successful. Response status -> 200 OK.")
	void testLoginSuccessful() throws Exception {
	    // arrange
		final JwtAuthenticationResponse expectedResponse = JwtAuthenticationResponse.Builder()
		                                                                            .accessToken("test_access_token")
		                                                                            .refreshToken("test_refresh_token")
		                                                                            .isSuccessful(Boolean.TRUE)
		                                                                            .build();
		// act
		when(userAuthenticationService.login(any(), any())).thenReturn(expectedResponse);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(loginEndpoint)
		                                                            .accept(MediaType.APPLICATION_JSON)
		                                                            .content(prepareAuthJSONRequest("test@email.com", "password"))
		                                                            .contentType(MediaType.APPLICATION_JSON);
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isOk())
		                                   .andReturn();
		// assert
        final JwtAuthenticationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);

		verify(userAuthenticationService, times(1)).login(any(), any());
        Assertions.assertAll(
                () -> assertEquals("test_access_token", actualResponse.getAccessToken()),
                () -> assertEquals("test_refresh_token", actualResponse.getRefreshToken()),
                () -> assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken()),
                () -> assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken()),
                () -> assertTrue(actualResponse.isSuccessful()),
                () -> assertEquals(expectedResponse.isSuccessful(), actualResponse.isSuccessful())
        );
	}

	@Test
    @DisplayName("Login failed. Empty email! Response status -> 400 Bad Request.")
	void testLoginFailed() throws Exception {
        // arrange
		final JwtAuthenticationResponse emptyResponse = new JwtAuthenticationResponse();
		final String jsonLoginRequest = prepareAuthJSONRequest("", "password");

		// act
		when(userAuthenticationService.login(any(), any())).thenReturn(emptyResponse);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(loginEndpoint)
		                                                            .accept(MediaType.APPLICATION_JSON)
		                                                            .content(jsonLoginRequest)
		                                                            .contentType(MediaType.APPLICATION_JSON);
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isBadRequest())
		                                   .andReturn();
		// assert
        final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidationResult = validator.validate(
                objectMapper.readValue(jsonLoginRequest, JwtAuthenticationRequest.class));
        final String actualResponse = mvcResult.getResponse().getContentAsString();

		verify(userAuthenticationService, times(1)).login(any(), any());
		Assertions.assertAll(
                () -> Assertions.assertNotNull(loginValidationResult),
                () -> Assertions.assertEquals("", actualResponse)
        );
    }

	@SuppressWarnings("Duplicates")
    private String prepareAuthJSONRequest(final String email, final String password) throws JsonProcessingException {
        final Encoder encoder = Base64.getEncoder();

        final String encodedEmail = Optional.ofNullable(email).map(notEmptyEmail -> encoder.encodeToString(notEmptyEmail.getBytes())).orElse(null);
        final String encodedPassword = Optional.ofNullable(password).map(notEmptyPassword -> encoder.encodeToString(notEmptyPassword.getBytes())).orElse(null);

        return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(encodedEmail)
                                                                       .password(encodedPassword)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }
}
