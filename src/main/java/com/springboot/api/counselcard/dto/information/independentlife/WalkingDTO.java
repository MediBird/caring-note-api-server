package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.independentlife.Walking;
import com.springboot.enums.WalkingEquipmentType;
import com.springboot.enums.WalkingType;
import java.util.Set;

public record WalkingDTO(
    @ValidEnum(enumClass = WalkingType.class)
    Set<WalkingType> walkingMethods,

    @ValidEnum(enumClass = WalkingEquipmentType.class)
    Set<WalkingEquipmentType> walkingEquipments,

    String walkingNote) {

    public WalkingDTO(Walking walking) {
        this(
            walking.getWalkingMethods(),
            walking.getWalkingEquipments(),
            walking.getWalkingNote()
        );
    }
}
