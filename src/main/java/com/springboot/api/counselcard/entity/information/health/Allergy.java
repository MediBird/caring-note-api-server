package com.springboot.api.counselcard.entity.information.health;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Allergy {
    private String allergyNote;

    public static Allergy from(AllergyDTO allergyDTO) {
        Allergy allergy = new Allergy();
        allergy.allergyNote = Objects.requireNonNullElse(allergyDTO.allergyNote(), "");
        return allergy;
    }

    public void update(AllergyDTO allergyDTO) {
        this.allergyNote = Objects.requireNonNullElse(allergyDTO.allergyNote(), this.allergyNote);
    }
}
