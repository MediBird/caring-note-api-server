package com.springboot.api.counselcard.dto.information.independentlife;

import java.util.List;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.independentlife.Walking;
import com.springboot.enums.WalkingEquipmentType;
import com.springboot.enums.WalkingType;

public record WalkingDTO(
    @ValidEnum(enumClass = WalkingType.class)
    List<WalkingType> walkingMethods,
        
    @ValidEnum(enumClass = WalkingEquipmentType.class)
    List<WalkingEquipmentType> walkingEquipments,
        
    String walkingNote) {

    public WalkingDTO(Walking walking) {
        this(
            walking.getWalkingMethods(),
            walking.getWalkingEquipments(),
            walking.getWalkingNote()
        );
    }
}
