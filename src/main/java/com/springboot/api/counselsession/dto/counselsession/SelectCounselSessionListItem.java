package com.springboot.api.counselsession.dto.counselsession;

import com.querydsl.core.annotations.QueryProjection;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class SelectCounselSessionListItem {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final String counselSessionId;
    private final String scheduledTime;
    private final String scheduledDate;
    private final String counseleeId;
    private final String counseleeName;
    private final String counselorId;
    private final String counselorName;
    @Schema(description = "상담 상태(SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED", example = "SCHEDULED")
    private final ScheduleStatus status;
    @Schema(description = "상담 카드 기록 상태(NOT_STARTED, IN_PROGRESS, COMPLETED", example = "NOT_STARTED")
    private final CardRecordStatus cardRecordStatus;
    private final Boolean isCounselorAssign;
    private final Boolean isConsent;

    @QueryProjection
    public SelectCounselSessionListItem(
        String counselSessionId,
        @NotNull
        LocalDateTime scheduledStartDateTime,
        String counseleeId,
        String counseleeName,
        String counselorId,
        String counselorName,
        ScheduleStatus status,
        CardRecordStatus cardRecordStatus,
        Boolean isConsent
    ) {
        this.counselSessionId = counselSessionId;
        this.scheduledTime = scheduledStartDateTime.toLocalTime().format(TIME_FORMATTER);
        this.scheduledDate = scheduledStartDateTime.toLocalDate().toString();
        this.counseleeId = counseleeId;
        this.counseleeName = counseleeName;
        this.counselorId = counselorId != null ? counselorId : "";
        this.counselorName = counselorName != null ? counselorName : "";
        this.status = status;
        this.cardRecordStatus = cardRecordStatus;
        this.isCounselorAssign = (counselorId != null);
        // TODO 차후에 모든 CounselSession에 CounseleeConsent가 있으면 제거해야함
        this.isConsent = isConsent != null ? isConsent : false;
    }
}