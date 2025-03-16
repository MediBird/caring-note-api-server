package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.enums.SmokingAmount;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Smoking {
    @Column(nullable = false)
    private String smokingPeriodNote;

    @Enumerated(EnumType.STRING)
    private SmokingAmount smokingAmount;

    public static Smoking initializeDefault() {
        Smoking smoking = new Smoking();
        smoking.smokingPeriodNote = "";
        smoking.smokingAmount = null;
        return smoking;
    }

    public static Smoking copy(Smoking smoking) {
        Smoking copiedSmoking = new Smoking();
        copiedSmoking.smokingPeriodNote = smoking.smokingPeriodNote;
        copiedSmoking.smokingAmount = smoking.smokingAmount;
        return copiedSmoking;
    }

    public void update(SmokingDTO smokingDTO) {
        this.smokingPeriodNote = Objects.requireNonNullElse(smokingDTO.smokingPeriodNote(), this.smokingPeriodNote);
        this.smokingAmount = Objects.requireNonNullElse(smokingDTO.smokingAmount(), this.smokingAmount);
    }
}
