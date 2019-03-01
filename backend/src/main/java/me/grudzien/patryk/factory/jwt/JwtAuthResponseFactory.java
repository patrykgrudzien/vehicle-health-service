package me.grudzien.patryk.factory.jwt;

import me.grudzien.patryk.domain.dto.login.JwtAuthenticationResponse;
import me.grudzien.patryk.factory.AbstractFactory;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static me.grudzien.patryk.factory.jwt.JwtAuthResponseType.FAILED;
import static me.grudzien.patryk.factory.jwt.JwtAuthResponseType.SUCCESS;

public final class JwtAuthResponseFactory implements AbstractFactory<JwtAuthResponseType, JwtAuthenticationResponse> {

	@Override
	public JwtAuthenticationResponse create(final JwtAuthResponseType jwtAuthResponseType, final Object... args) {
		final JwtAuthenticationResponse.JwtAuthenticationResponseBuilder builder = JwtAuthenticationResponse.Builder();
		return Match(jwtAuthResponseType).of(
				Case($(is(SUCCESS)), () -> builder.isSuccessful(TRUE)
				                                  .accessToken((String) args[0])
				                                  .refreshToken((String) args[1])
				                                  .build()
				),
				Case($(is(FAILED)), () -> builder.isSuccessful(FALSE).build())
		);
	}
}
