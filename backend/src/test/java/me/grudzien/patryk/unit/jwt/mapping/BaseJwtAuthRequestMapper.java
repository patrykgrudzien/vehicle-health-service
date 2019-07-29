package me.grudzien.patryk.unit.jwt.mapping;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;

abstract class BaseJwtAuthRequestMapper {

    private static final Encoder encoder = Base64.getEncoder();

    static final String TEST_EMAIL = "admin.root@gmail.com";
    static final String TEST_PASSWORD = "admin";

    static BaseJwtAuthRequestMapper.Encoding prepareLoginRequest(final String email, final String password) {
        return doEncoding -> JwtAuthenticationRequest.Builder()
                                                     .email(doEncoding ? encodeNotNullValue(email) : email)
                                                     .password(doEncoding ? encodeNotNullValue(password) : password)
                                                     .build();
    }

    @FunctionalInterface
    interface Encoding {
        JwtAuthenticationRequest doEncoding(boolean doEncoding);
    }

    private static String encodeNotNullValue(final String value) {
        return Optional.ofNullable(value)
                       .map(notEmptyValue -> encoder.encodeToString(notEmptyValue.getBytes()))
                       .orElse(null);
    }
}
