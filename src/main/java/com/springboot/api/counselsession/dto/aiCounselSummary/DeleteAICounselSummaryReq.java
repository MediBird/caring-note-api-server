package com.springboot.api.dto.aiCounselSummary;

import jakarta.validation.constraints.NotBlank;

public record DeleteAICounselSummaryReq(@NotBlank String counselSessionId) {
}
