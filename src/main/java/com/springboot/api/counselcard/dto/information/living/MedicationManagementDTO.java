package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.MedicationManagement;
import com.springboot.enums.MedicationAssistant;

import java.util.Set;

public record MedicationManagementDTO(
        Boolean isMedicationManagement) {

    public MedicationManagementDTO (MedicationManagement medicationManagement){
        this(
            medicationManagement.getIsMedicationManagement()
        );
    }
}
