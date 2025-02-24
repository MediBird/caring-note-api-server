package com.springboot.api.dto.counselsession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounselSessionStatRes {

    private long totalSessionCount;
    private long counseleeCountForThisMonth;
    private long totalCaringMessageCount;
    private long counselHoursForThisMonth;

}
