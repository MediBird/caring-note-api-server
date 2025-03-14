package com.springboot.api.counselsession.dto.counselsession;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselee.entity.Counselee;
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
                @Schema(description = "상담 상태(SCHEDULED, PROGRESS, COMPLETED, CANCELED", example = "SCHEDULED") ScheduleStatus status,
                @Schema(description = "상담 카드 기록 상태(UNRECORDED, RECORDING, RECORDED", example = "UNRECORDED") CardRecordStatus cardRecordStatus,
                boolean isCounselorAssign) {

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
                                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalTime()
                                                .format(timeFormatter))
                                .counselSessionId(counselSession.getId())
                                .isCounselorAssign(Optional.ofNullable(counselSession.getCounselor()).isPresent())
                                .status(counselSession.getStatus())
//                                .cardRecordStatus(Optional.ofNullable(counselSession.getCounselCard())
//                                                .map(CounselCard::getCardRecordStatus)
//                                                .orElse(CardRecordStatus.UNRECORDED))
                                .build();
        }
}
