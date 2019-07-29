package me.grudzien.patryk.unit.authentication.resource;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.NONE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import lombok.NoArgsConstructor;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;

import me.grudzien.patryk.BaseResource;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;

@NoArgsConstructor(access = NONE)
abstract class BaseAuthenticationResource extends BaseResource {

	static final String TEST_EMAIL = "test@email.com";
	static final String TEST_PASSWORD = "password";

	/**
	 * URI(s) for {@link me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions}
	 */
	static final String AUTHENTICATION_LOGIN_URI = fromPath(AUTHENTICATION_RESOURCE_ROOT).path(LOGIN).toUriString();

	// -- request

	static RequestBuilder loginPostRequestBuilder(final String jsonContent) {
		return MockMvcRequestBuilders.post(AUTHENTICATION_LOGIN_URI)
		                      .accept(APPLICATION_JSON)
		                      .content(jsonContent)
		                      .contentType(APPLICATION_JSON);
	}

	// -- payload

	static JwtAuthenticationResponse createLoginResponse() {
		return JwtAuthenticationResponse.Builder()
		                                .accessToken(randomAlphanumeric(25))
		                                .refreshToken(randomAlphanumeric(25))
		                                .isSuccessful(TRUE)
		                                .build();
	}

	static LoginRequestWithEncoding createLoginRequest(final String email, final String password) {
		return doEncoding -> buildJwtAuthRequest(email, password, doEncoding);
	}

	static LoginJsonRequestWithEncoding createLoginJsonRequest(final String email, final String password) {
		return doEncoding -> tryConvertObjectToJson(buildJwtAuthRequest(email, password, doEncoding));
	}

	@FunctionalInterface
	interface LoginRequestWithEncoding {
		JwtAuthenticationRequest doEncoding(boolean doEncoding);

	}

	@FunctionalInterface
	interface LoginJsonRequestWithEncoding {
		String doEncoding(boolean doEncoding);
	}

	private static JwtAuthenticationRequest buildJwtAuthRequest(final String email, final String password,
	                                                            final boolean doEncoding) {
		return JwtAuthenticationRequest.Builder()
		                               .email(doEncoding ? encodeNotNullValue(email) : email)
		                               .password(doEncoding ? encodeNotNullValue(password) : password)
		                               .build();
	}
}
