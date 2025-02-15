package com.springboot.api.dto.counselsession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounselSessionStatRes {

    private int totalSessionCount;
    private int counseleeCountForThisMonth;
    private int totalCaringMessageCount;
    private int counselHoursForThisMonth;

}
