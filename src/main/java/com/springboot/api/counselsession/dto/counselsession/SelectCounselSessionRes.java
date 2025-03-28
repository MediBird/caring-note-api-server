package com.springboot.api.counselsession.dto.counselsession;

import java.util.Optional;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.ScheduleStatus;

import lombok.Builder;

@Builder
public record SelectCounselSessionRes(
                String counselSessionId,
                String scheduledTime,
                String scheduledDate,
                String counseleeId,
                String counseleeName,
                String counselorId,
                String counselorName,
                Integer sessionNumber,
                ScheduleStatus status) {
        public static SelectCounselSessionRes from(CounselSession counselSession) {
                return SelectCounselSessionRes.builder()
                                .counselSessionId(counselSession.getId())
                                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalTime().toString())
                                .scheduledDate(counselSession.getScheduledStartDateTime().toLocalDate().toString())
                                .counseleeId(counselSession.getCounselee().getId())
                                .counseleeName(counselSession.getCounselee().getName())
                                .counselorId(Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getId)
                                                .orElse(""))
                                .counselorName(Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getName)
                                                .orElse(""))
                                .sessionNumber(counselSession.getSessionNumber())
                                .status(counselSession.getStatus())
                                .build();
        }
}
