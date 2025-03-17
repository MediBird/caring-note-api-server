package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.Smoking;
import com.springboot.enums.SmokingAmount;

public record SmokingDTO(
        String smokingPeriodNote,
        @ValidEnum(enumClass = SmokingAmount.class) SmokingAmount smokingAmount) {

    public SmokingDTO(Smoking smoking) {
        this(
                smoking.getSmokingPeriodNote(),
                smoking.getSmokingAmount());
    }
}