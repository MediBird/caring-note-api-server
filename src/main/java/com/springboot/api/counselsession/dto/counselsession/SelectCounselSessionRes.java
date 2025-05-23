package com.springboot.api.counselsession.dto.counselsession;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.ScheduleStatus;
import lombok.Getter;

@Getter
public class SelectCounselSessionRes {

    private String counselSessionId;
    private String scheduledTime;
    private String scheduledDate;
    private String counseleeId;
    private String counseleeName;
    private String counselorId;
    private String counselorName;
    private String sessionNumber;
    private ScheduleStatus status;

    public static SelectCounselSessionRes from(CounselSession counselSession) {
        SelectCounselSessionRes res = new SelectCounselSessionRes();

        res.counselSessionId = counselSession.getId();
        res.scheduledTime = counselSession.getScheduledStartDateTime().toLocalTime().toString();
        res.scheduledDate = counselSession.getScheduledStartDateTime().toLocalDate().toString();

        res.counseleeId = counselSession.getCounselee().getId();
        res.counseleeName = counselSession.getCounselee().getName();

        Counselor counselor = counselSession.getCounselor();
        res.counselorId = (counselor != null) ? counselor.getId() : "";
        res.counselorName = (counselor != null) ? counselor.getName() : "";

        res.status = counselSession.getStatus();
        res.sessionNumber = res.status == ScheduleStatus.CANCELED ? "-" :
            String.valueOf(counselSession.getSessionNumber());

        return res;
    }

}
