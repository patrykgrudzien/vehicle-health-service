package me.grudzien.patryk.domain.dto.login;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 6971598280333003305L;

	private final String accessToken;
	private final String refreshToken;
}
