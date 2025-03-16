package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Smoking {
    private Boolean isSmoking;

    public static Smoking initializeDefault() {
        Smoking smoking = new Smoking();
        smoking.isSmoking = false;
        return smoking;
    }

    public static Smoking copy(Smoking smoking) {
        Smoking copiedSmoking = new Smoking();
        copiedSmoking.isSmoking = smoking.isSmoking;
        return copiedSmoking;
    }

    public void update(SmokingDTO smokingDTO) {
        this.isSmoking = Objects.requireNonNullElse(smokingDTO.isSmoking(), this.isSmoking);
    }
}
