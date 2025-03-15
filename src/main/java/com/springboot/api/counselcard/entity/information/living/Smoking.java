package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.enums.SmokingAmount;
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

    private String smokingPeriodNote;
    @Enumerated(EnumType.STRING)
    private SmokingAmount smokingAmount;

    public static Smoking from(SmokingDTO smokingDTO) {
        Smoking smoking = new Smoking();
        smoking.smokingPeriodNote = Objects.requireNonNullElse(smokingDTO.smokingPeriodNote(), "");
        smoking.smokingAmount = Objects.requireNonNullElse(smokingDTO.smokingAmount(), SmokingAmount.NONE);
        return smoking;
    }

    public void update(SmokingDTO smokingDTO) {
        this.smokingPeriodNote = Objects.requireNonNullElse(smokingDTO.smokingPeriodNote(), this.smokingPeriodNote);
        this.smokingAmount = Objects.requireNonNullElse(smokingDTO.smokingAmount(), this.smokingAmount);
    }
}
