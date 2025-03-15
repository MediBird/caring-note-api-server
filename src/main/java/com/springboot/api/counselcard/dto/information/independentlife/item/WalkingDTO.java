package com.springboot.api.counselcard.dto.information.independentlife.item;

import java.util.List;

import lombok.Builder;

@Builder
public record WalkingDTO(
        List<String> walkingMethods, List<String> walkingEquipments, String etcNote) {
}
