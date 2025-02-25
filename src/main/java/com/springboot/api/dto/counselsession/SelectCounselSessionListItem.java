package com.springboot.api.dto.counselsession;


import com.springboot.api.domain.*;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
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

    public static SelectCounselSessionListItem from(CounselSession counselSession) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return SelectCounselSessionListItem
                .builder()
                .counseleeId(Optional.ofNullable(counselSession.getCounselee())
                        .map(BaseEntity::getId)
                        .orElse(""))
                .counselorName(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getName)
                        .orElse(""))
                .counselorId(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getId)
                        .orElse(""))
                .counseleeName(Optional.ofNullable(counselSession.getCounselee())
                        .map(Counselee::getName)
                        .orElse(""))
                .scheduledDate(counselSession.getScheduledStartDateTime().toLocalDate().toString())
                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalTime().format(timeFormatter))
                .counselSessionId(counselSession.getId())
                .isCounselorAssign(Optional.ofNullable(counselSession.getCounselor()).isPresent())
                .status(counselSession.getStatus())
                .cardRecordStatus(Optional.ofNullable(counselSession.getCounselCard())
                        .map(CounselCard::getCardRecordStatus)
                        .orElse(CardRecordStatus.UNRECORDED))
                .build();
    }
}
