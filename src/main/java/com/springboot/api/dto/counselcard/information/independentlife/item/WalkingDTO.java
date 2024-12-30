package com.springboot.api.dto.counselcard.information.independentlife.item;

import lombok.Builder;

import java.util.List;

@Builder
public record WalkingDTO(
        List<String> walkingMethods
        , List<String> walkingEquipments
        , String etcNote
){
}
