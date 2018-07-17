package me.grudzien.patryk.domain.dto.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 6971598280333003305L;

	private final String accessToken;
	private final String refreshToken;
}
