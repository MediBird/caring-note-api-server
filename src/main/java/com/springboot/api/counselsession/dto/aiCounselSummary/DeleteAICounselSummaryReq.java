package com.springboot.api.counselsession.dto.aiCounselSummary;

import jakarta.validation.constraints.NotBlank;

public record DeleteAICounselSummaryReq(@NotBlank String counselSessionId) {
}
