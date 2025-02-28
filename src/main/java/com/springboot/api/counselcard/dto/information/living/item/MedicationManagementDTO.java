package com.springboot.api.counselcard.dto.information.living.item;

import java.util.List;

import lombok.Builder;

@Builder
public record MedicationManagementDTO(
        boolean isAlone, String houseMateNote, List<String> medicationAssistants) {
}
