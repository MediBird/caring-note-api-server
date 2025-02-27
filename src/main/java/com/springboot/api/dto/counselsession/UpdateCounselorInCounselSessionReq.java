package com.springboot.api.dto.counselsession;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record UpdateCounselorInCounselSessionReq(
        @NotBlank(message = "상담 세션 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다") String counselSessionId,
        @NotBlank(message = "상담자 ID는 필수 입력값입니다") @Size(min = 26, max = 26, message = "상담자 ID는 26자여야 합니다") String counselorId) {
}
