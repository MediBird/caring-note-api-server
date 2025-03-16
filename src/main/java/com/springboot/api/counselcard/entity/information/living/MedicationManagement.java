package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.enums.MedicationAssistant;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MedicationManagement {
    @Column(nullable = false)
    private String houseMateNote;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<MedicationAssistant> medicationAssistants;

    public static MedicationManagement initializeDefault() {
        MedicationManagement medicationManagement = new MedicationManagement();
        medicationManagement.houseMateNote = "";
        medicationManagement.medicationAssistants = Set.of();
        return medicationManagement;
    }

    public static MedicationManagement copy(MedicationManagement medicationManagement) {
        MedicationManagement copiedMedicationManagement = new MedicationManagement();
        copiedMedicationManagement.houseMateNote = medicationManagement.houseMateNote;
        copiedMedicationManagement.medicationAssistants = Set.copyOf(medicationManagement.medicationAssistants);
        return copiedMedicationManagement;
    }

    public void update(MedicationManagementDTO medicationManagementDTO) {
        this.houseMateNote = Objects.requireNonNullElse(medicationManagementDTO.houseMateNote(), this.houseMateNote);
        this.medicationAssistants = Objects.requireNonNullElse(medicationManagementDTO.medicationAssistants(), this.medicationAssistants);
    }
}
