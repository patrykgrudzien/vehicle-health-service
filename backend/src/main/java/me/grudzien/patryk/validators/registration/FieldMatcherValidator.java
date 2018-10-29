package me.grudzien.patryk.validators.registration;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.concurrent.atomic.AtomicBoolean;

import static me.grudzien.patryk.utils.log.LogMarkers.EXCEPTION_MARKER;

@Log4j2
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
		final AtomicBoolean valid = new AtomicBoolean(true);
		final String[] firstInputFieldValue = new String[1];
		final String[] secondInputFieldValue = new String[1];

		Try.run(() -> {
			firstInputFieldValue[0] = BeanUtils.getProperty(fieldValue, firstFieldName);
			secondInputFieldValue[0] = BeanUtils.getProperty(fieldValue, secondFieldName);
		})
		   .onSuccess(successVoid -> valid.set(firstInputFieldValue[0] == null && secondInputFieldValue[0] == null ||
		                                       firstInputFieldValue[0] != null && firstInputFieldValue[0].equals(secondInputFieldValue[0])))
		   .onFailure(throwable -> log.error(EXCEPTION_MARKER, "Error ({}) occurred on BeanUtils.getProperty() in ({}).", throwable.getMessage(),
		                                     FieldMatcherValidator.class.getSimpleName()));
		if (!valid.get()) {
			constraintValidatorContext.buildConstraintViolationWithTemplate(message)
			                          .addConstraintViolation()
			                          .disableDefaultConstraintViolation();
		}
		return valid.get();
	}
}
