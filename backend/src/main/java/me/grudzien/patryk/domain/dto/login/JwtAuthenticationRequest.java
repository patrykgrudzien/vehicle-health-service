package me.grudzien.patryk.domain.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 3441108122105659002L;

	private String email;
	private String password;
	private String refreshToken;
}
