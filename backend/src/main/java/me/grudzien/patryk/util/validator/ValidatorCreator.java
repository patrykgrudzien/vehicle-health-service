package me.grudzien.patryk.util.validator;

import javax.validation.Validation;
import javax.validation.Validator;

public interface ValidatorCreator {

	static Validator getDefaultValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}
}
