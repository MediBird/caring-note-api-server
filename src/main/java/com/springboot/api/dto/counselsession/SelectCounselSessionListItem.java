package com.springboot.api.dto.counselsession;


import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "상담 상태(SCHEDULED, PROGRESS, COMPLETED, CANCELED", example = "SCHEDULED")
    private ScheduleStatus status;
    @Schema(description = "상담 카드 기록 상태(UNRECORDED, RECORDING, RECORDED", example = "UNRECORDED")
    private CardRecordStatus cardRecordStatus;
    private boolean isCounselorAssign;
}
