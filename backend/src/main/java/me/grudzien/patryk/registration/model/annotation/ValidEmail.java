package me.grudzien.patryk.registration.model.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.grudzien.patryk.registration.model.annotation.validation.EmailValidator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {

	// value of this String comes from -> resources/locale/messages_xx.properties
	String message() default "valid-email-message-default";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
