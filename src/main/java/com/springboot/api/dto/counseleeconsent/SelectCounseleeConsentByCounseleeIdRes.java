package com.springboot.api.dto.counseleeconsent;

import java.time.LocalDateTime;

public record SelectCounseleeConsentByCounseleeIdRes(
                String counseleeConsentId, String counseleeId, String counseleeName, LocalDateTime consentDateTime,
                boolean isConsent) {
}
