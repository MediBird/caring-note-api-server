package com.springboot.api.counselsession.dto.counselsession;

import java.time.LocalDate;

public record SelectPreviousCounselSessionListRes(

        String counselSessionId, Integer CounselSessionOrder, LocalDate counselSessionDate, String counselorName,
        boolean isShardCaringMessage) {
}
