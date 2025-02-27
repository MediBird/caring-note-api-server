package com.springboot.api.dto.counselsession;

import com.springboot.enums.ScheduleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStatusInCounselSessionReq(
        @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
        @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
        String counselSessionId,
        @NotNull(message = "상담 상태는 필수 입력값입니다")
        ScheduleStatus status) {
}
