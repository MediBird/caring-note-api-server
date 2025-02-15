package com.springboot.api.dto.aiCounselSummary;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertSpeechToTextReq {
    @NotBlank
    private String counselSessionId;
}
