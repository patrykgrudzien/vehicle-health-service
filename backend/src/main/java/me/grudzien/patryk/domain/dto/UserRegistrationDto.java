package me.grudzien.patryk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import me.grudzien.patryk.validators.FieldMatcher;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatcher.List(multipleFieldsMatchers = {
		@FieldMatcher(first = "password", second = "confirmedPassword", message = "The password fields must match"),
		@FieldMatcher(first = "email", second = "confirmedEmail", message = "The email fields must match")
})
public class UserRegistrationDto {

	@NotNull
	@NotEmpty
	private String firstName;

	@NotNull
	@NotEmpty
	private String lastName;

	@NotNull
	@NotEmpty
	@Email(message = "Provided email has incorrect format.")
	private String email;

	@NotNull
	@NotEmpty
	@Email(message = "Provided email has incorrect format.")
	private String confirmedEmail;

	@NotNull
	@NotEmpty
	private String password;

	@NotNull
	@NotEmpty
	private String confirmedPassword;
}