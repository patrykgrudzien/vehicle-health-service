package me.grudzien.patryk.domain.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationEntryPointResponse {

	private String code;
	private String message;
}
