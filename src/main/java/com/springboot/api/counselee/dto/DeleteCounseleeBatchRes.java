package com.springboot.api.counselee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteCounseleeBatchRes(
    @NotBlank String deletedCounseleeId) {

}
