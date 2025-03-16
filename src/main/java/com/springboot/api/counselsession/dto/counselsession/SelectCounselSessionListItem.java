package com.springboot.api.counselsession.dto.counselsession;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SelectCounselSessionListItem(
                String counselSessionId,
                String scheduledTime,
                String scheduledDate,
                String counseleeId,
                String counseleeName,
                String counselorId,
                String counselorName,
                @Schema(description = "상담 상태(SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED", example = "SCHEDULED") ScheduleStatus status,
                @Schema(description = "상담 카드 기록 상태(NOT_STARTED, IN_PROGRESS, COMPLETED", example = "NOT_STARTED") CardRecordStatus cardRecordStatus,
                boolean isCounselorAssign) {

        public static SelectCounselSessionListItem from(CounselSession counselSession, CardRecordStatus cardRecordStatus) {

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                return SelectCounselSessionListItem
                                .builder()
                                .counseleeId(counselSession.getCounselee().getId())
                                .counselorName(Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getName)
                                                .orElse(""))
                                .counselorId(Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getId)
                                                .orElse(""))
                                .counseleeName(counselSession.getCounselee().getName())
                                .scheduledDate(counselSession.getScheduledStartDateTime().toLocalDate().toString())
                                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalTime().format(timeFormatter))
                                .counselSessionId(counselSession.getId())
                                .isCounselorAssign(Optional.ofNullable(counselSession.getCounselor()).isPresent())
                                .status(counselSession.getStatus())
                                .cardRecordStatus(Optional.ofNullable(cardRecordStatus).orElse(CardRecordStatus.NOT_STARTED))
                                .build();
        }
}
