package com.springboot.api.dto.counselsession;

import lombok.Builder;
import lombok.Getter;

public record CounselSessionStatRes(long totalSessionCount, long counseleeCountForThisMonth, long totalCaringMessageCount, long counselHoursForThisMonth) {
    @Builder
    public CounselSessionStatRes {
    }
}
