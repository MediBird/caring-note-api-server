package com.springboot.api.dto.counselcard.information.base.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BaseInfoDTO(
        String counseleeId
        ,String name
        , @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate
        , String counselSessionOrder
        , @JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastCounselDate
){}
