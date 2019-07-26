package me.grudzien.patryk.unit.authentication.resource;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.io.IOException;
import java.util.Set;

import me.grudzien.patryk.TestsUtils;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.resource.AuthenticationResourceImpl;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.utils.validation.ValidationService;
import me.grudzien.patryk.utils.validation.to.remove.CustomValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static me.grudzien.patryk.TestsUtils.DISABLE_ENCODING;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;

@WebMvcTest(controllers = AuthenticationResourceImpl.class, secure = false)
class AuthenticationResourceImplTest {

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

	@MockBean
	private UserAuthenticationService userAuthenticationService;

    @MockBean
	private JwtAuthenticationRequestMapper authRequestMapper;

    @MockBean
	private ValidationService validationService;

    private final Validator validator = CustomValidator.getDefaultValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "password";

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
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(AUTHENTICATION_RESOURCE_ROOT + LOGIN)
		                                                            .accept(MediaType.APPLICATION_JSON)
		                                                            .content(TestsUtils.prepareAuthJSONBody(TEST_EMAIL, TEST_PASSWORD, DISABLE_ENCODING))
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
		final String jsonLoginRequest = TestsUtils.prepareAuthJSONBody("", TEST_PASSWORD, DISABLE_ENCODING);

		// when
		when(userAuthenticationService.login(any(), any())).thenReturn(new JwtAuthenticationResponse());
        final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(AUTHENTICATION_RESOURCE_ROOT + LOGIN)
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
        final String jsonLoginRequest = TestsUtils.prepareAuthJSONBody(TEST_EMAIL, "", DISABLE_ENCODING);

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
                          .post(AUTHENTICATION_RESOURCE_ROOT + LOGIN)
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
}