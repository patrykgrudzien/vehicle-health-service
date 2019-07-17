package me.grudzien.patryk.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import me.grudzien.patryk.registration.model.annotation.ValidEmail;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 5980286062602002568L;

	@NotNull(message = "email-cannot-be-null")
	@NotEmpty(message = "email-cannot-be-empty")
	@ValidEmail
	private String email;

	@NotNull(message = "password-cannot-be-null")
	@NotEmpty(message = "password-cannot-be-empty")
	private String password;

	private String refreshToken;
	private String idToken;
}
