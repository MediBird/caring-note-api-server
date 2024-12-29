package com.springboot.api.dto.counselcard.information.living.item;

import lombok.Builder;

import java.util.List;

@Builder
public record MedicationManagementDTO(
        boolean isAlone
        , String houseMateNote
        , List<String> medicationAssistants
){ }
