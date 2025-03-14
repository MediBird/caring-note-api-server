package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.MedicationManagement;
import com.springboot.enums.MedicationAssistant;

import java.util.Set;
import lombok.Builder;

public record MedicationManagementDTO(
        String houseMateNote,
        @ValidEnum(enumClass = MedicationAssistant.class)
        Set<MedicationAssistant> medicationAssistants) {

    public MedicationManagementDTO (MedicationManagement medicationManagement){
        this(
            medicationManagement.getHouseMateNote(),
            medicationManagement.getMedicationAssistants()
        );
    }
}
