package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.WalkingEquipmentType;
import com.springboot.enums.WalkingType;
import java.util.Set;

public record WalkingDTO(
    @ValidEnum(enumClass = WalkingType.class)
    Set<WalkingType> walkingMethods,

    @ValidEnum(enumClass = WalkingEquipmentType.class)
    Set<WalkingEquipmentType> walkingEquipments,

    String walkingNote) {

}
