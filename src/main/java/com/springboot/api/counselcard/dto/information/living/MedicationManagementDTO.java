package com.springboot.api.counselcard.dto.information.living;

import java.util.List;

import com.springboot.api.counselcard.entity.information.living.MedicationManagement;
import com.springboot.enums.MedicationAssistant;

public record MedicationManagementDTO(
        Boolean isAlone,
        String houseMateNote,
        List<MedicationAssistant> medicationAssistants) {

    public MedicationManagementDTO(MedicationManagement medicationManagement) {
        this(
                medicationManagement.getIsAlone(),
                medicationManagement.getHouseMateNote(),
                medicationManagement.getMedicationAssistants());
    }
}
