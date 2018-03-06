package me.grudzien.patryk.validators;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.reflect.InvocationTargetException;

public class FieldMatcherValidator implements ConstraintValidator<FieldMatcher, Object> {

	private String firstFieldName;
	private String secondFieldName;
	private String message;

	@Override
	public void initialize(final FieldMatcher annotation) {
		this.firstFieldName = annotation.first();
		this.secondFieldName = annotation.second();
		this.message = annotation.message();
	}

	@Override
	public boolean isValid(final Object fieldValue, final ConstraintValidatorContext constraintValidatorContext) {

		boolean valid = true;

		try {
			final String firstInputFieldValue = BeanUtils.getProperty(fieldValue, firstFieldName);
			final String secondInputFieldValue = BeanUtils.getProperty(fieldValue, secondFieldName);

			valid = firstInputFieldValue == null && secondInputFieldValue == null ||
			        firstInputFieldValue != null && firstInputFieldValue.equals(secondInputFieldValue);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
			exception.printStackTrace();
		}

		if (!valid) {
			constraintValidatorContext.buildConstraintViolationWithTemplate(message)
			                          .addPropertyNode(firstFieldName)
			                          .addConstraintViolation()
			                          .disableDefaultConstraintViolation();
		}
		return valid;
	}
}
