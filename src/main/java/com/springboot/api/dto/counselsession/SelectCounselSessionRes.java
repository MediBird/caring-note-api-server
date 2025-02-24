package com.springboot.api.dto.counselsession;


import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
@Builder
public class SelectCounselSessionRes {

    private String counselSessionId;

    private String scheduledTime;

    private String scheduledDate;

    private String counseleeId;

    private String counseleeName;

    private String counselorId;

    private String counselorName;

    public static SelectCounselSessionRes from(CounselSession counselSession) {
        return SelectCounselSessionRes
                .builder()
                .counselSessionId(counselSession.getId())
                .scheduledTime(counselSession.getScheduledStartDateTime().toLocalDate().toString())
                .scheduledDate(counselSession.getScheduledStartDateTime().toLocalTime().toString())
                .counseleeId(Optional.ofNullable(counselSession.getCounselee())
                        .map(Counselee::getId)
                        .orElse(""))
                .counseleeName(Optional.ofNullable(counselSession.getCounselee())
                        .map(Counselee::getName)
                        .orElse(""))
                .counselorId(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getId)
                        .orElse(""))
                .counselorName(Optional.ofNullable(counselSession.getCounselor())
                        .map(Counselor::getName)
                        .orElse(""))
                .build();
    }

}
