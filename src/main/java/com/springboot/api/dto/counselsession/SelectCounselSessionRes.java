package com.springboot.api.dto.counselsession;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectCounselSessionRes {

    private String counselSessionId;

    private String scheduledTime;

    private String scheduledDate;

    private String counseleeId;

    private String counseleeName;

    private String counselorId;

    private String counselorName;

}
