package me.grudzien.patryk.registration.model.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.grudzien.patryk.registration.model.validation.FieldMatcherValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldMatcherValidator.class)
@Documented
public @interface FieldMatcher {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String first();

	String second();

	@Target({TYPE, ANNOTATION_TYPE})
	@Retention(RUNTIME)
	@Documented
	@interface List {
		FieldMatcher[] value();
	}
}
