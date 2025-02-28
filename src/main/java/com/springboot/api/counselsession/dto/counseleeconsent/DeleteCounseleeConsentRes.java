package com.springboot.api.counselsession.dto.counseleeconsent;

import lombok.Builder;

@Builder
public record DeleteCounseleeConsentRes(String deletedCounseleeConsentId) {
}
