package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MedicationManagement {
    private Boolean isMedicationManagement;

    public static MedicationManagement initializeDefault() {
        MedicationManagement medicationManagement = new MedicationManagement();
        medicationManagement.isMedicationManagement = false;
        return medicationManagement;
    }

    public static MedicationManagement copy(MedicationManagement medicationManagement) {
        MedicationManagement copiedMedicationManagement = new MedicationManagement();
        copiedMedicationManagement.isMedicationManagement = medicationManagement.isMedicationManagement;
        return copiedMedicationManagement;
    }

    public void update(MedicationManagementDTO medicationManagementDTO) {
        this.isMedicationManagement = Objects.requireNonNullElse(medicationManagementDTO.isMedicationManagement(), this.isMedicationManagement);
    }
}
