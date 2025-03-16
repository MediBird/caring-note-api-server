package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Smoking;

public record SmokingDTO(
    Boolean isSmoking
) {

    public SmokingDTO (Smoking smoking){
        this(
            smoking.getIsSmoking()
        );
    }
}
