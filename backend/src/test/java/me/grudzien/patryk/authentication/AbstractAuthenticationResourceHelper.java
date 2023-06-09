package me.grudzien.patryk.authentication;

import lombok.NoArgsConstructor;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import me.grudzien.patryk.ObjectMapperEncoder;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.NONE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;

@NoArgsConstructor(access = NONE)
public abstract class AbstractAuthenticationResourceHelper extends ObjectMapperEncoder {

    protected static final String TEST_EMAIL = "admin.root@gmail.com";
    protected static final String TEST_PASSWORD = "admin";
    protected static final String HTTP_LOCALHOST = "http://localhost";

    /**
	 * URI(s) for {@link AuthenticationResourceDefinitions}
	 */
	protected static final String AUTHENTICATION_LOGIN_URI = fromPath(AUTHENTICATION_RESOURCE_ROOT).path(LOGIN).toUriString();

	protected static RequestBuilder loginPostRequestBuilder(final String jsonContent) {
		return MockMvcRequestBuilders.post(AUTHENTICATION_LOGIN_URI)
                                     .accept(APPLICATION_JSON)
                                     .content(jsonContent)
                                     .contentType(APPLICATION_JSON);
	}

	protected static JwtAuthenticationResponse createLoginResponse() {
		return JwtAuthenticationResponse.Builder()
                                        .accessToken(randomAlphanumeric(25))
                                        .refreshToken(randomAlphanumeric(25))
                                        .isSuccessful(TRUE)
                                        .build();
	}

	protected static LoginRequestWithEncoding createLoginRequest(final String email, final String password) {
		return doEncoding -> buildJwtAuthRequest(email, password, doEncoding);
	}

    protected static LoginJsonRequestWithEncoding createLoginJsonRequest(final String email, final String password) {
        return doEncoding -> tryConvertObjectToJson(buildJwtAuthRequest(email, password, doEncoding));
    }

	@FunctionalInterface
	protected interface LoginRequestWithEncoding {
		JwtAuthenticationRequest doEncoding(boolean doEncoding);
	}

    @FunctionalInterface
    protected interface LoginJsonRequestWithEncoding {
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
