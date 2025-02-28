package com.springboot.api.counselsession.dto.counseleeconsent;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record SelectCounseleeConsentByCounseleeIdRes(
        String counseleeConsentId, String counseleeId, String counseleeName, String counselSessionId,
        LocalDateTime consentDateTime, boolean isConsent) {
}
