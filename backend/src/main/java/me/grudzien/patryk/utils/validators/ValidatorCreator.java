package me.grudzien.patryk.utils.validators;

import javax.validation.Validation;
import javax.validation.Validator;

public interface ValidatorCreator {

	static Validator getDefaultValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}
}
