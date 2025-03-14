package com.springboot.api.counselcard.dto.information.base;

import com.springboot.api.counselee.entity.Counselee;
import com.springboot.enums.HealthInsuranceType;
import java.util.Objects;

public record BaseInfoDTO(
        String counseleeId,
        String counseleeName,
        String birthDate,
        String lastCounselDate,
        HealthInsuranceType healthInsuranceType
) {

    public BaseInfoDTO(Counselee counselee) {
        this(
            counselee.getId(),
            counselee.getName(),
            counselee.getDateOfBirth().toString(),
            Objects.requireNonNullElse(counselee.getLastCounselDate(), "").toString(),
            counselee.getHealthInsuranceType()
        );
    }
}
