package com.springboot.api.counselsession.dto.counselsession;

import java.util.Optional;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;

public record SelectCounselSessionRes(
                String counselSessionId,
                String scheduledTime,
                String scheduledDate,
                String counseleeId,
                String counseleeName,
                String counselorId,
                String counselorName,
                Integer sessionNumber) {
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
                                                .orElse(0));
        }
}
