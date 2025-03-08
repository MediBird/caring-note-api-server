package com.springboot.api.counselsession.dto.counselsession;

import java.util.Optional;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.ScheduleStatus;


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
        // TODO null 검사를 진행해야하는지 확인
        public static SelectCounselSessionRes from(CounselSession counselSession) {
                return new SelectCounselSessionRes(
                                counselSession.getId(),
                                counselSession.getScheduledStartDateTime().toLocalDate().toString(),
                                counselSession.getScheduledStartDateTime().toLocalTime().toString(),
                                counselSession.getCounselee().getId(),
                                counselSession.getCounselee().getName(),
                                Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getId)
                                                .orElse(""),
                                Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getName)
                                                .orElse(""),
                                Optional.ofNullable(counselSession.getSessionNumber())
                                                .orElse(0),
                                counselSession.getStatus());
                                }
}
