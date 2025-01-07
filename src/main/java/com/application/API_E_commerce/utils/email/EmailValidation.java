package com.application.API_E_commerce.utils.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValidation {
  String message() default "Invalid email format.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
