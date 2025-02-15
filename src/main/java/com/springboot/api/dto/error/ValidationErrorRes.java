package com.springboot.api.dto.error;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationErrorRes {
    private List<ValidationError> errors;
}