package com.springboot.api.dto.counselsession;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.springboot.api.domain.BaseEntity;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SelectCounselSessionListItem {
        private final String counselSessionId;
        private final String scheduledTime;
        private final String scheduledDate;
        private final String counseleeId;
        private final String counseleeName;
        private final String counselorId;
        private final String counselorName;
        @Schema(description = "상담 상태(SCHEDULED, PROGRESS, COMPLETED, CANCELED", example = "SCHEDULED")
        private final ScheduleStatus status;
        @Schema(description = "상담 카드 기록 상태(UNRECORDED, RECORDING, RECORDED", example = "UNRECORDED")
        private final CardRecordStatus cardRecordStatus;
        private final boolean isCounselorAssign;

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
                                .cardRecordStatus(Optional.ofNullable(counselSession.getCounselCard())
                                                .map(CounselCard::getCardRecordStatus)
                                                .orElse(CardRecordStatus.UNRECORDED))
                                .build();
        }
}
