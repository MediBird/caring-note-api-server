package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.MedicationManagement;

public record MedicationManagementDTO(
        Boolean isMedicationManagement) {

    public MedicationManagementDTO (MedicationManagement medicationManagement){
        this(
            medicationManagement.getIsMedicationManagement()
        );
    }
}
