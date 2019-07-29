package me.grudzien.patryk.integration.authentication.resource;

import io.vavr.CheckedFunction0;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions;

import static lombok.AccessLevel.NONE;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.AUTHENTICATION_RESOURCE_ROOT;
import static me.grudzien.patryk.authentication.resource.AuthenticationResourceDefinitions.LOGIN;

@NoArgsConstructor(access = NONE)
abstract class BaseAuthenticationResourceIT {

    private static final Encoder encoder = Base64.getEncoder();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static final String TEST_EMAIL = "admin.root@gmail.com";
    static final String TEST_PASSWORD = "admin";

    /**
     * URI(s) for {@link AuthenticationResourceDefinitions}
     */
    static final String AUTHENTICATION_LOGIN_URI = fromPath(AUTHENTICATION_RESOURCE_ROOT).path(LOGIN).toUriString();

    static AuthRequestWithEncoding createLoginRequest() {
        return BaseAuthenticationResourceIT::buildJwtAuthRequest;
    }

    static JsonRequestWithEncoding createLoginJsonRequest(final String email, final String password) {
        return doEncoding -> tryConvertToJson(buildJwtAuthRequest(email, password, doEncoding));
    }

    @FunctionalInterface
    interface AuthRequestWithEncoding {
        JwtAuthenticationRequest doEncoding(boolean doEncoding);
    }

    @FunctionalInterface
    interface JsonRequestWithEncoding {
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

    private static String encodeNotNullValue(final String value) {
        return Optional.ofNullable(value)
                       .map(notEmptyValue -> encoder.encodeToString(notEmptyValue.getBytes()))
                       .orElse(null);
    }

    private static String tryConvertToJson(final Object value) {
        return CheckedFunction0.liftTry(() -> objectMapper.writeValueAsString(value))
                               .apply()
                               .get();
    }
}
