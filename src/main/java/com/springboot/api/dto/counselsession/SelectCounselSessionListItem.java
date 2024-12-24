package com.springboot.api.dto.counselsession;


import com.springboot.enums.CardRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SelectCounselSessionListItem {
    private String counselSessionId;
    private String scheduledTime;
    private String scheduledDate;
    private String counseleeId;
    private String counseleeName;
    private String counselorId;
    private String counselorName;
    private CardRecordStatus cardRecordStatus;
    private boolean isCounselorAssign;
}
