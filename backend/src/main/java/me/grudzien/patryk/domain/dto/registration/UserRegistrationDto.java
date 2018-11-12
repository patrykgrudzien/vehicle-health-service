package me.grudzien.patryk.domain.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import me.grudzien.patryk.domain.enums.registration.RegistrationProvider;
import me.grudzien.patryk.annotation.FieldMatcher;
import me.grudzien.patryk.annotation.ValidEmail;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@FieldMatcher.List({
		@FieldMatcher(first = "password", second = "confirmedPassword", message = "password-fields-must-match"),
		@FieldMatcher(first = "email", second = "confirmedEmail", message = "email-fields-must-match")
})
public class UserRegistrationDto {

	@NotNull(message = "first-name-cannot-be-null")
	@NotEmpty(message = "first-name-cannot-be-empty")
	private String firstName;

	@NotNull(message = "last-name-cannot-be-null")
	@NotEmpty(message = "last-name-cannot-be-empty")
	private String lastName;

	@NotNull(message = "email-cannot-be-null")
	@NotEmpty(message = "email-cannot-be-empty")
	@ValidEmail
	private String email;

	@NotNull(message = "confirmed-email-cannot-be-null")
	@NotEmpty(message = "confirmed-email-cannot-be-empty")
	@ValidEmail
	private String confirmedEmail;

	@NotNull(message = "password-cannot-be-null")
	@NotEmpty(message = "password-cannot-be-empty")
	private String password;

	@NotNull(message = "confirmed-password-cannot-be-null")
	@NotEmpty(message = "confirmed-password-cannot-be-empty")
	private String confirmedPassword;

	private String profilePictureUrl;
	private RegistrationProvider registrationProvider;
}