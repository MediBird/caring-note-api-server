package com.springboot.api.dto.counselee;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteCounseleeBatchRes(
        @NotBlank String deletedCounseleeId) {
}
