package com.springboot.api.counselsession.dto.counselsession;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStartTimeInCounselSessionReq(
    @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    String counselSessionId,
    @Schema(description = "상담 일정 날짜 및 시간", example = "2024-11-23 14:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotBlank(message = "상담 일정 날짜 및 시간은 필수 입력값입니다")
    String scheduledStartDateTime
) {
}
