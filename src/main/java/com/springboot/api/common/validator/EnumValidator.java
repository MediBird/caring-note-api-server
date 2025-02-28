package com.springboot.api.common.validator;

import java.util.stream.Stream;

import com.springboot.api.common.annotation.ValidEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // 값이 null이면 유효하지 않음
        }
        return Stream.of(enumClass.getEnumConstants())
                .anyMatch(enumValue -> enumValue.name().equals(value.name()));
    }
}