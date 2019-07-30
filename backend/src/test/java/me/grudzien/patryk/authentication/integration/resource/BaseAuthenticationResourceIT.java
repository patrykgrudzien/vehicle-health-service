package me.grudzien.patryk.authentication.integration.resource;

import lombok.NoArgsConstructor;

import me.grudzien.patryk.ObjectMapperEncoder;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions;

import static lombok.AccessLevel.NONE;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;

@NoArgsConstructor(access = NONE)
abstract class BaseAuthenticationResourceIT extends ObjectMapperEncoder {

    static final String TEST_EMAIL = "admin.root@gmail.com";
    static final String TEST_PASSWORD = "admin";

    /**
     * URI(s) for {@link AuthenticationResourceDefinitions}
     */
    static final String AUTHENTICATION_LOGIN_URI = fromPath(AUTHENTICATION_RESOURCE_ROOT).path(LOGIN).toUriString();

    static LoginRequestWithEncoding createLoginRequest() {
        return BaseAuthenticationResourceIT::buildJwtAuthRequest;
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

    private static JwtAuthenticationRequest buildJwtAuthRequest(final boolean doEncoding) {
        return buildJwtAuthRequest(TEST_EMAIL, TEST_PASSWORD, doEncoding);
    }

    private static JwtAuthenticationRequest buildJwtAuthRequest(final String email, final String password,
                                                                final boolean doEncoding) {
        return JwtAuthenticationRequest.Builder()
                                       .email(doEncoding ? encodeNotNullValue(email) : email)
                                       .password(doEncoding ? encodeNotNullValue(password) : password)
                                       .build();
    }
}
