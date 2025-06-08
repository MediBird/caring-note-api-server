package com.springboot.api.counselsession.dto.counselsession;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record SelectPreviousCounselSessionDetailRes(
    String counselSessionId,
    LocalDate counselSessionDate,
    Integer sessionNumber,
    String counselorName,
    String medicationCounselRecord,
    String aiSummary
) {
} 