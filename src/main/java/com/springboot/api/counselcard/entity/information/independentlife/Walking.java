package com.springboot.api.counselcard.entity.information.independentlife;

import java.util.List;
import java.util.Objects;

import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;
import com.springboot.enums.WalkingEquipmentType;
import com.springboot.enums.WalkingType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Walking {
    @ElementCollection
    @CollectionTable(name = "walking_methods", joinColumns = @JoinColumn(name = "walking_id"))
    @Enumerated(EnumType.STRING)
    private List<WalkingType> walkingMethods;

    @ElementCollection
    @CollectionTable(name = "walking_equipments", joinColumns = @JoinColumn(name = "walking_id"))
    @Enumerated(EnumType.STRING)
    private List<WalkingEquipmentType> walkingEquipments;

    String walkingNote;

    public static Walking initializeDefault() {
        Walking walking = new Walking();
        walking.walkingMethods = List.of();
        walking.walkingEquipments = List.of();
        walking.walkingNote = "";
        return walking;
    }

    public static Walking copy(Walking walking) {
        Walking copiedWalking = new Walking();
        copiedWalking.walkingMethods = List.copyOf(walking.walkingMethods);
        copiedWalking.walkingEquipments = List.copyOf(walking.walkingEquipments);
        copiedWalking.walkingNote = walking.walkingNote;
        return copiedWalking;
    }

    public void update(WalkingDTO walking) {
        this.walkingMethods = Objects.requireNonNullElse(walking.walkingMethods(), this.walkingMethods);
        this.walkingEquipments = Objects.requireNonNullElse(walking.walkingEquipments(), this.walkingEquipments);
        this.walkingNote = Objects.requireNonNullElse(walking.walkingNote(), this.walkingNote);
    }
}
