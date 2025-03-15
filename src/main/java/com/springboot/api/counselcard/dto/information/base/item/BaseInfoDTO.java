package com.springboot.api.counselcard.dto.information.base.item;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.enums.HealthInsuranceType;

import lombok.Builder;

@Builder
public record BaseInfoDTO(
        String counseleeId, String name, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
        String counselSessionOrder, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastCounselDate,
        HealthInsuranceType healthInsuranceType) {
}
