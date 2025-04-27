package com.springboot.api.counselsession.dto.counseleeconsent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCounseleeConsentReq {

    @NotBlank(message = "내담자 동의 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "내담자 동의 ID는 26자여야 합니다")
    private String counseleeConsentId;

    @NotNull(message = "동의 여부는 필수 입력값입니다")
    private boolean isConsent;
}
