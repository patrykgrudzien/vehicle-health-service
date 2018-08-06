package me.grudzien.patryk.domain.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 6971598280333003305L;

	private String accessToken;
	private String refreshToken;
	private boolean isSuccessful;
}
