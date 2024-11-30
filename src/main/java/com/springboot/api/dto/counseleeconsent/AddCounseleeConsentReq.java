package com.springboot.api.dto.counseleeconsent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounseleeConsentReq {

    @NotBlank
    private String counselSessionId;

    @NotBlank
    private String counseleeId;

    @NotNull
    private boolean isConsent;
}
