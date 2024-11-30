package com.springboot.api.dto.counseleeconsent;

import java.time.LocalDateTime;

public record SelectCounseleeConsentRes(
        String counseleeConsentId
        , String counseleeId
        , String counseleeName
        , String counselSessionId
        , LocalDateTime consentDateTime
        , boolean isConsent
){}
