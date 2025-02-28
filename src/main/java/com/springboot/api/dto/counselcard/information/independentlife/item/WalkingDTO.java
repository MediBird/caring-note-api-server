package com.springboot.api.dto.counselcard.information.independentlife.item;

import java.util.List;

import lombok.Builder;

@Builder
public record WalkingDTO(
                List<String> walkingMethods, List<String> walkingEquipments, String etcNote) {
}
