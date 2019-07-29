package me.grudzien.patryk.unit.jwt.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapper;
import me.grudzien.patryk.authentication.mapping.JwtAuthenticationRequestMapperImpl;
import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.utils.web.ObjectDecoder;
import me.grudzien.patryk.utils.web.RequestsDecoder;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JwtAuthenticationRequestMapperImpl.class, RequestsDecoder.class})
class JwtAuthenticationRequestMapperTest extends BaseJwtAuthRequestMapper {

    @Autowired
    private JwtAuthenticationRequestMapper jwtAuthenticationRequestMapper;

    @Test
    void toDecodedAuthRequest() {
        // given
        final JwtAuthenticationRequest encodedAuthRequest = prepareLoginRequest(TEST_EMAIL, TEST_PASSWORD).doEncoding(true);

        // when
        final JwtAuthenticationRequest decodedAuthRequest = ObjectDecoder.authRequestDecoder().apply(encodedAuthRequest, jwtAuthenticationRequestMapper);

        // then
        Assertions.assertAll(
                () -> assertThat(decodedAuthRequest.getEmail()).isEqualTo(TEST_EMAIL),
                () -> assertThat(decodedAuthRequest.getPassword()).isEqualTo(TEST_PASSWORD),
                () -> assertThat(decodedAuthRequest.getIdToken()).isNullOrEmpty(),
                () -> assertThat(decodedAuthRequest.getRefreshToken()).isNullOrEmpty()
        );
    }
}