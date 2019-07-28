package me.grudzien.patryk.authentication.model.factory;

import lombok.extern.log4j.Log4j2;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationRequest;
import me.grudzien.patryk.authentication.model.factory.JwtAuthRequestFactory.JwtAuthType;
import me.grudzien.patryk.utils.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static java.lang.String.valueOf;

import static me.grudzien.patryk.authentication.model.factory.JwtAuthRequestFactory.JwtAuthType.EMAIL_PASSWORD_REQUEST;
import static me.grudzien.patryk.authentication.model.factory.JwtAuthRequestFactory.JwtAuthType.FULL_REQUEST;

@Log4j2
public final class JwtAuthRequestFactory implements AbstractFactory<JwtAuthType, JwtAuthenticationRequest> {

    private JwtAuthenticationRequest.Builder authRequest = JwtAuthenticationRequest.Builder();

    @Override
    public JwtAuthenticationRequest create(final JwtAuthType jwtAuthType, final Object... args) {
        return Match(jwtAuthType).of(
                Case($(is(FULL_REQUEST)), () -> authRequest.email(valueOf(args[0]))
                                                           .password(valueOf(args[1]))
                                                           .idToken(valueOf(args[2]))
                                                           .refreshToken(valueOf(args[3]))
                                                           .build()),
                Case($(is(EMAIL_PASSWORD_REQUEST)), () -> authRequest.email(valueOf(args[0]))
                                                                     .password(valueOf(args[1]))
                                                                     .build())
        );
    }

    public enum JwtAuthType {
        FULL_REQUEST,
        EMAIL_PASSWORD_REQUEST
    }
}
