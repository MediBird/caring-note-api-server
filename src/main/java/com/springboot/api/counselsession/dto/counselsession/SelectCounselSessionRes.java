package com.springboot.api.counselsession.dto.counselsession;

import java.util.Optional;

import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;

public record SelectCounselSessionRes(
                String counselSessionId,
                String scheduledTime,
                String scheduledDate,
                String counseleeId,
                String counseleeName,
                String counselorId,
                String counselorName) {
        // TODO null 검사를 진행해야하는지 확인
        public static SelectCounselSessionRes from(CounselSession counselSession) {
                return new SelectCounselSessionRes(
                                counselSession.getId(),
                                counselSession.getScheduledStartDateTime().toLocalDate().toString(),
                                counselSession.getScheduledStartDateTime().toLocalTime().toString(),
                                Optional.ofNullable(counselSession.getCounselee())
                                                .map(Counselee::getId)
                                                .orElse(""),
                                Optional.ofNullable(counselSession.getCounselee())
                                                .map(Counselee::getName)
                                                .orElse(""),
                                Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getId)
                                                .orElse(""),
                                Optional.ofNullable(counselSession.getCounselor())
                                                .map(Counselor::getName)
                                                .orElse(""));
        }
}
