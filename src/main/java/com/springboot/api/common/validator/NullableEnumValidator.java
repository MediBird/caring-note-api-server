package com.springboot.api.common.validator;

import com.springboot.api.common.annotation.ValidNullableEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

public class NullableEnumValidator implements ConstraintValidator<ValidNullableEnum, Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidNullableEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Stream.of(enumClass.getEnumConstants())
            .anyMatch(enumValue -> enumValue.name().equals(value.name()));
    }
}