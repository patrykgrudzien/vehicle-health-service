package me.grudzien.patryk.jwt.model.factory;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory.JwtAuthResponseType;
import me.grudzien.patryk.utils.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static java.lang.Boolean.TRUE;

import static me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse.JwtAuthenticationResponseBuilder;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory.JwtAuthResponseType.FAILED_RESPONSE;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseFactory.JwtAuthResponseType.SUCCESS_ACCESS_REFRESH_TOKEN_RESPONSE;

public final class JwtAuthResponseFactory implements AbstractFactory<JwtAuthResponseType, JwtAuthenticationResponse> {

    private final JwtAuthenticationResponseBuilder builder = JwtAuthenticationResponse.Builder();

	@Override
	public JwtAuthenticationResponse create(final JwtAuthResponseType jwtAuthResponseType, final Object... args) {
		return Match(jwtAuthResponseType).of(
				Case($(is(SUCCESS_ACCESS_REFRESH_TOKEN_RESPONSE)), () -> builder.isSuccessful(TRUE)
                                                                                .accessToken((String) args[0])
                                                                                .refreshToken((String) args[1])
                                                                                .build()
				),
				Case($(is(FAILED_RESPONSE)), builder::build)
		);
	}

    public enum JwtAuthResponseType {
        FAILED_RESPONSE,
        SUCCESS_ACCESS_REFRESH_TOKEN_RESPONSE
    }
}
