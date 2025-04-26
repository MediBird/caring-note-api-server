package com.springboot.api.counselsession.dto.aiCounselSummary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertSpeechToTextReq {

    @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    private String counselSessionId;
}
