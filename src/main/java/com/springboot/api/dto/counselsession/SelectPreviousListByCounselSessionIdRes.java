package com.springboot.api.dto.counselsession;


import java.time.LocalDate;

public record SelectPreviousListByCounselSessionIdRes(

        String counselSessionId
        , String CounselSessionOrder
        , LocalDate counselSessionDate
        , String counselorName
        , boolean isShardCaringMessage
        ) {
}
