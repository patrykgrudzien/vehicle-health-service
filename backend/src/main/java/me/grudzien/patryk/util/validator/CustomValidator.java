package me.grudzien.patryk.util.validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

public interface CustomValidator {

	static Validator getDefaultValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

	static <T> List<String> getTranslatedValidationResult(final T input, final LocaleMessagesCreator localeMessagesCreator) {
		final Set<ConstraintViolation<T>> validation = getDefaultValidator().validate(input);
		return validation.stream()
		                 .map(ConstraintViolation::getMessage)
		                 // I'm checking two fields for email and two for password but there is
		                 // no need to duplicate the same message
		                 .distinct()
		                 // translate "messageCode" to i18n message
		                 .map(localeMessagesCreator::buildLocaleMessage)
		                 .collect(Collectors.toList());
	}
}
