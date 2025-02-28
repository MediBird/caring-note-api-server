package com.springboot.api.counselsession.dto.counselsession;

import lombok.Builder;

public record CounselSessionStatRes(long totalSessionCount, long counseleeCountForThisMonth,
        long totalCaringMessageCount, long counselHoursForThisMonth) {
    @Builder
    public CounselSessionStatRes {
    }
}
