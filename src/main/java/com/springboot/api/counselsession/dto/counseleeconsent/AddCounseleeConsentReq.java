package com.springboot.api.dto.counseleeconsent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounseleeConsentReq {

    @NotBlank
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    private String counselSessionId;

    @NotBlank
    @Size(min = 26, max = 26, message = "내담자 ID는 26자여야 합니다")
    private String counseleeId;

    @NotNull(message = "동의 여부는 필수 입력값입니다")
    private boolean isConsent;
}
