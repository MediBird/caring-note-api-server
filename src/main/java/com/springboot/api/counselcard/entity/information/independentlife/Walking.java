package com.springboot.api.counselcard.entity.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;
import com.springboot.enums.WalkingEquipmentType;
import com.springboot.enums.WalkingType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Walking {
    Set<WalkingType> walkingMethods;

    Set<WalkingEquipmentType> walkingEquipments;

    String walkingNote;

    public static Walking from(WalkingDTO walkingDTO) {
        Walking walking = new Walking();
        walking.walkingMethods = Objects.requireNonNullElse(walkingDTO.walkingMethods(), Set.of());
        walking.walkingEquipments = Objects.requireNonNullElse(walkingDTO.walkingEquipments(), Set.of());
        walking.walkingNote = Objects.requireNonNullElse(walkingDTO.walkingNote(), "");
        return walking;
    }

    public void update(WalkingDTO walking) {
        this.walkingMethods = Objects.requireNonNullElse(walking.walkingMethods(), this.walkingMethods);
        this.walkingEquipments = Objects.requireNonNullElse(walking.walkingEquipments(), this.walkingEquipments);
        this.walkingNote = Objects.requireNonNullElse(walking.walkingNote(), this.walkingNote);
    }
}
