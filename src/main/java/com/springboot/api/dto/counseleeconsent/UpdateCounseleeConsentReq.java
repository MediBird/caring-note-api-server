package com.springboot.api.dto.counseleeconsent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCounseleeConsentReq {
    private String counseleeConsentId;
    private boolean isConsent;
}
