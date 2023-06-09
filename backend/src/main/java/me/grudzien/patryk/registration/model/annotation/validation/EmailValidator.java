package me.grudzien.patryk.registration.model.annotation.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.grudzien.patryk.registration.model.annotation.ValidEmail;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

	@Override
	public void initialize(final ValidEmail constraintAnnotation) {}

	@Override
	public boolean isValid(final String email, final ConstraintValidatorContext context) {
		return validateEmail(email);
	}

	private boolean validateEmail(final String email) {
		final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		final Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
