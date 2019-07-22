package me.grudzien.patryk.jwt.model.factory;

import me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse;
import me.grudzien.patryk.utils.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static java.lang.Boolean.TRUE;

import static me.grudzien.patryk.authentication.model.dto.JwtAuthenticationResponse.*;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseType.FAILED_RESPONSE;
import static me.grudzien.patryk.jwt.model.factory.JwtAuthResponseType.SUCCESS_RESPONSE;

public final class JwtAuthResponseFactory implements AbstractFactory<JwtAuthResponseType, JwtAuthenticationResponse> {

	@Override
	public JwtAuthenticationResponse create(final JwtAuthResponseType jwtAuthResponseType, final Object... args) {
		final JwtAuthenticationResponseBuilder builder = Builder();
		return Match(jwtAuthResponseType).of(
				Case($(is(SUCCESS_RESPONSE)), () -> builder.isSuccessful(TRUE)
                                                           .accessToken((String) args[0])
                                                           .refreshToken((String) args[1])
                                                           .build()
				),
				Case($(is(FAILED_RESPONSE)), builder::build)
		);
	}
}
