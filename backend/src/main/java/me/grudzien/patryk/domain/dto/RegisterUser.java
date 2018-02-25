package me.grudzien.patryk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterUser {

	private @NotNull String name;
	private @NotNull String lastName;
	private @NotNull String email;
	private @NotNull String password;
	private @NotNull String confirmedPassword;
}
