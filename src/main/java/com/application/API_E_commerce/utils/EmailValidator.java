package com.application.API_E_commerce.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
          "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"
  );

  @Override
  public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }
}
