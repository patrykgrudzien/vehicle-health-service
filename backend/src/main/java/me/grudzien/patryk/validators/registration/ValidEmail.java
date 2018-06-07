package me.grudzien.patryk.validators.registration;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {

	// value of this String comes from -> resources/locale/messages_xx.properties
	String message() default "valid-email-message-default";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
