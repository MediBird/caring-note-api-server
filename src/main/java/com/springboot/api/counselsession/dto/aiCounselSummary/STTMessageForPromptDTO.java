package com.springboot.api.counselsession.dto.aiCounselSummary;

import lombok.Builder;

@Builder
public record STTMessageForPromptDTO(String speaker, String text) {

}
