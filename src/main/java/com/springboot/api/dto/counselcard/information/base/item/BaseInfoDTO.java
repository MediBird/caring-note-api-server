package com.springboot.api.dto.counselcard.information.base.item;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BaseInfoDTO(
        String counseleeId
        ,String name
        , LocalDate birthDate
        , String counselSessionOrder
        , LocalDate lastCounselDate
){}
