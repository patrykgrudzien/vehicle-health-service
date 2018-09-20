package me.grudzien.patryk.oauth2.domain;

import me.grudzien.patryk.domain.dto.login.JwtUser;

public class CustomOAuth2OidcPrincipalUserFactory {

	private CustomOAuth2OidcPrincipalUserFactory() {
		throw new UnsupportedOperationException("Creating object of this class is not allowed!");
	}

	public static CustomOAuth2OidcPrincipalUser create(final JwtUser jwtUser) {
		return CustomOAuth2OidcPrincipalUser.Builder(jwtUser)
		                                    .build();
	}
}
