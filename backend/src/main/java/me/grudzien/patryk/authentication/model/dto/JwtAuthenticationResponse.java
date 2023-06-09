package me.grudzien.patryk.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder", builderClassName = "Builder")
@JsonInclude(NON_NULL)
public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 6971598280333003305L;

	private String accessToken;
	private String refreshToken;

	@JsonProperty("successful")
	private boolean isSuccessful;
}
