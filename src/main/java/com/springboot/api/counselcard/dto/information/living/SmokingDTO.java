package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Smoking;
import com.springboot.enums.SmokingAmount;

public record SmokingDTO(
    String smokingPeriodNote,
    SmokingAmount smokingAmount) {

    public SmokingDTO(Smoking smoking) {
        this(
            smoking != null ? smoking.getSmokingPeriodNote() : null,
            smoking != null ? smoking.getSmokingAmount() : null);
    }
}