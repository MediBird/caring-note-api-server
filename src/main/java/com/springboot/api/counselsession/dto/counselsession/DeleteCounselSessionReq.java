package com.springboot.api.counselsession.dto.counselsession;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeleteCounselSessionReq {
    @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    private String counselSessionId;
}
