package me.grudzien.patryk.unit.controller.login;

import static io.jsonwebtoken.lang.Assert.notEmpty;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import me.grudzien.patryk.config.custom.CustomApplicationProperties;
import me.grudzien.patryk.controller.login.UserAuthenticationController;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;
import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.service.login.UserAuthenticationService;
import me.grudzien.patryk.util.validator.ValidatorCreator;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserAuthenticationController.class, secure = false)
public class UserAuthenticationControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomApplicationProperties customApplicationProperties;

	@MockBean
	private UserAuthenticationService userAuthenticationService;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private Validator validator;

	@Before
	public void setUp() {
		validator = ValidatorCreator.getDefaultValidator();
	}

	@Test
	public void testSuccessfulLogin() throws Exception {
		final JwtAuthenticationResponse expectedResponse = JwtAuthenticationResponse.Builder()
		                                                                            .accessToken("test_access_token")
		                                                                            .refreshToken("test_refresh_token")
		                                                                            .isSuccessful(Boolean.TRUE)
		                                                                            .build();
		// when
		when(userAuthenticationService.login(any(), any())).thenReturn(expectedResponse);
		// login request
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder().email("email").password("password").refreshToken("test_refresh_token").build();
		// json conversion
		final String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);
		// request builder
		final RequestBuilder requestBuilder = post(customApplicationProperties.getEndpoints().getAuthentication().getRoot())
													  .accept(MediaType.APPLICATION_JSON).content(jsonLoginRequest)
				                                      .contentType(MediaType.APPLICATION_JSON);
		// mock call
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isOk())
		                                   .andReturn();

		final JwtAuthenticationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);

		// then
		verify(userAuthenticationService, times(1)).login(any(), any());
		assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken());
		assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
		assertEquals(expectedResponse.isSuccessful(), actualResponse.isSuccessful());
	}

	@Test
	public void testLoginEmptyEmail() throws Exception {
		// expected response
		final JwtAuthenticationResponse emptyResponse = new JwtAuthenticationResponse();
		// login request
		final JwtAuthenticationRequest loginRequest = JwtAuthenticationRequest.Builder().email("").password("password").refreshToken("test_refresh_token").build();

		final Set<ConstraintViolation<JwtAuthenticationRequest>> loginValidation = validator.validate(loginRequest);
		notEmpty(loginValidation);

		// when
		when(userAuthenticationService.login(any(), any())).thenReturn(emptyResponse);
		// json conversion
		final String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);
		// request builder
		final RequestBuilder requestBuilder = post(customApplicationProperties.getEndpoints().getAuthentication().getRoot())
													  .accept(MediaType.APPLICATION_JSON).content(jsonLoginRequest)
				                                      .contentType(MediaType.APPLICATION_JSON);
		// mock call
		final MvcResult mvcResult = mockMvc.perform(requestBuilder)
		                                   .andDo(print())
		                                   .andExpect(status().isNoContent())
		                                   .andReturn();
	}
}
