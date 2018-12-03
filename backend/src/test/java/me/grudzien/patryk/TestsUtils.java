package me.grudzien.patryk;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationRequest;

public final class TestsUtils {

    private TestsUtils() {
        throw new UnsupportedOperationException("Creating object of this class is not allowed!");
    }

    public static String prepareAuthJSONRequest(final String email, final String password, final boolean doEncoding) throws JsonProcessingException {
        final Encoder encoder = Base64.getEncoder();
        final ObjectMapper objectMapper = new ObjectMapper();

        final String encodedEmail = Optional.ofNullable(email).map(notEmptyEmail -> encoder.encodeToString(notEmptyEmail.getBytes())).orElse(null);
        final String encodedPassword = Optional.ofNullable(password).map(notEmptyPassword -> encoder.encodeToString(notEmptyPassword.getBytes())).orElse(null);

        return objectMapper.writeValueAsString(JwtAuthenticationRequest.Builder()
                                                                       .email(doEncoding ? encodedEmail : email)
                                                                       .password(doEncoding ? encodedPassword : password)
                                                                       .refreshToken(RandomStringUtils.randomAlphanumeric(25))
                                                                       .build());
    }
}
