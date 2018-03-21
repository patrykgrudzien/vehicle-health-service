package me.grudzien.patryk.domain.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import me.grudzien.patryk.validators.registration.FieldMatcher;
import me.grudzien.patryk.validators.registration.ValidEmail;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldMatcher.List({
		@FieldMatcher(first = "password", second = "confirmedPassword", message = "The password fields must match."),
		@FieldMatcher(first = "email", second = "confirmedEmail", message = "The email fields must match.")
})
public class UserRegistrationDto {

	@NotNull(message = "First name cannot be null.")
	@NotEmpty(message = "First name cannot be empty.")
	private String firstName;

	@NotNull(message = "Last name cannot be null.")
	@NotEmpty(message = "Last name cannot be empty.")
	private String lastName;

	@NotNull(message = "Email address cannot be null.")
	@NotEmpty(message = "Email address cannot be empty.")
	@ValidEmail
	private String email;

	@NotNull(message = "Confirmed email address cannot be null.")
	@NotEmpty(message = "Confirmed email address cannot be empty.")
	@ValidEmail
	private String confirmedEmail;

	@NotNull(message = "Password cannot be null.")
	@NotEmpty(message = "Password cannot be empty.")
	private String password;

	@NotNull(message = "Confirmed password cannot be null.")
	@NotEmpty(message = "Confirmed password cannot be empty.")
	private String confirmedPassword;
}