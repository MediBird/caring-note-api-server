package com.springboot.api.counselsession.dto.counselsession;

import lombok.Builder;

public record CounselSessionStatRes(long counselHoursThisMonth, long counseleeCountForThisMonth,
                                    long medicationCounselCountThisYear, long counselorCountThisYear) {

    @Builder
    public CounselSessionStatRes {
    }
}
