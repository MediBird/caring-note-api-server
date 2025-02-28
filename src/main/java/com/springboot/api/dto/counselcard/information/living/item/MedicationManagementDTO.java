package com.springboot.api.dto.counselcard.information.living.item;

import java.util.List;

import lombok.Builder;

@Builder
public record MedicationManagementDTO(
                boolean isAlone, String houseMateNote, List<String> medicationAssistants) {
}
