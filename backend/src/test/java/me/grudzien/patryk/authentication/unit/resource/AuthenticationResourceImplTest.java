package me.grudzien.patryk.authentication.unit.resource;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mobile.device.Device;
import org.springframework.test.web.servlet.MvcResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.resource.impl.AuthenticationResourceImpl;
import me.grudzien.patryk.authentication.service.UserAuthenticationService;
import me.grudzien.patryk.utils.web.model.ExceptionResponse;

import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
	controllers = AuthenticationResourceImpl.class,
	secure = false
)
class AuthenticationResourceImplTest extends BaseAuthenticationResource {

    @MockBean
	private UserAuthenticationService userAuthenticationService;

    @Test
    @DisplayName("Login successful. Response status: 200 OK.")
	void shouldReturn200withAccessAndRefreshTokensOnLogin() throws Exception {
	    // given
	    final JwtAuthenticationRequest decodedAuthRequest = createLoginRequest(TEST_EMAIL, TEST_PASSWORD).doEncoding(false);
	    final JwtAuthenticationResponse expectedResponse = createLoginResponse();
		setupAuthMapperToReturn(decodedAuthRequest);
	    given(userAuthenticationService.login(any(JwtAuthenticationRequest.class), any(Device.class))).willReturn(expectedResponse);

		// when
	    final MvcResult mvcResult = mockMvc.perform(loginPostRequestBuilder(tryConvertObjectToJson(decodedAuthRequest)))
		                                   .andDo(print())
		                                   .andExpect(status().isOk())
		                                   .andReturn();
		// then
        final JwtAuthenticationResponse actualResponse = tryConvertJsonToObject(mvcResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);
        assertAll(
        		() -> assertThat(actualResponse.getAccessToken()).hasSize(25),
                () -> assertThat(actualResponse.getRefreshToken()).hasSize(25),
		        () -> assertThat(actualResponse.getAccessToken()).isEqualTo(expectedResponse.getAccessToken()),
		        () -> assertThat(actualResponse.getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken()),
		        () -> assertThat(actualResponse.isSuccessful()).isTrue(),
		        () -> assertThat(actualResponse.isSuccessful()).isEqualTo(expectedResponse.isSuccessful())
        );
        verifyAuthMapper();
        verify(userAuthenticationService).login(any(JwtAuthenticationRequest.class), any(Device.class));
	}

	@Test
    @DisplayName("Login failed. Empty email! Response status: 400 Bad Request.")
	void shouldReturn400onLoginWhenEmptyEmail() throws Exception {
        // given
		final JwtAuthenticationRequest decodedAuthRequest = createLoginRequest("", TEST_PASSWORD).doEncoding(false);
		setupAuthMapperToReturn(decodedAuthRequest);
		given(userAuthenticationService.login(any(JwtAuthenticationRequest.class), any(Device.class))).willReturn(null);

		// when
		final MvcResult mvcResult = mockMvc.perform(loginPostRequestBuilder(tryConvertObjectToJson(decodedAuthRequest)))
		                                   .andDo(print())
		                                   .andExpect(status().isBadRequest())
		                                   .andReturn();
		// then
		final ExceptionResponse exceptionResponse = tryConvertJsonToObject(mvcResult.getResponse().getContentAsString(), ExceptionResponse.class);
		assertThat(exceptionResponse).isNotNull();
		assertThat(exceptionResponse.getErrors()).isNotEmpty();
		assertThat(exceptionResponse.getErrors()).hasSize(2);
		verifyAuthMapper();
        verify(userAuthenticationService, never()).login(any(JwtAuthenticationRequest.class), any(Device.class));
    }

    @Test
    @DisplayName("Login failed. Empty password! Response status: 400 Bad Request.")
    void shouldReturn400onLoginWhenEmptyPassword() {
	    // given
	    final JwtAuthenticationRequest decodedAuthRequest = createLoginRequest(TEST_EMAIL, "").doEncoding(false);
	    setupAuthMapperToReturn(decodedAuthRequest);
	    given(userAuthenticationService.login(any(JwtAuthenticationRequest.class), any(Device.class))).willReturn(null);

        // when - then
        RestAssuredMockMvc.given()
                          .webAppContextSetup(webApplicationContext)
                          .log().all()
                          .body(tryConvertObjectToJson(decodedAuthRequest))
                          .contentType(JSON)
                          .accept(JSON)
                          .when()
                          .post(AUTHENTICATION_LOGIN_URI)
                          .then()
                          .log().all()
                          .statusCode(BAD_REQUEST.value())
                          .body("message", notNullValue())
                          .body("errors", notNullValue());
        verifyAuthMapper();
	    verify(userAuthenticationService, never()).login(any(), any());
    }
}
