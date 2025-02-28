package com.springboot.api.dto.aiCounselSummary;

import lombok.Builder;

@Builder
public record STTMessageForPromptDTO(String speaker, String text) {
}
