package com.springboot.api.counselcard.entity.information.living;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.enums.SmokingAmount;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Smoking {
    private String smokingPeriodNote;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SmokingAmount smokingAmount;

    public static Smoking initializeDefault() {
        Smoking smoking = new Smoking();
        smoking.smokingPeriodNote = null;
        smoking.smokingAmount = SmokingAmount.NONE;
        return smoking;
    }

    public static Smoking copy(Smoking smoking) {
        Smoking copiedSmoking = new Smoking();
        copiedSmoking.smokingPeriodNote = smoking.smokingPeriodNote;
        copiedSmoking.smokingAmount = smoking.smokingAmount;
        return copiedSmoking;
    }

    public void update(SmokingDTO smokingDTO) {
        if(Objects.isNull(smokingDTO)) {
            return;
        }
        this.smokingPeriodNote = smokingDTO.smokingPeriodNote();
        this.smokingAmount = Objects.requireNonNullElse(smokingDTO.smokingAmount(), SmokingAmount.NONE);
    }
}