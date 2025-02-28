package com.springboot.api.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationErrorRes {
    private List<ValidationError> errors;
}