package com.springboot.api.counselcard.entity.information.living;

import java.util.List;
import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.enums.MedicationAssistant;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MedicationManagement {
    private Boolean isAlone;

    private String houseMateNote;

    @ElementCollection
    @CollectionTable(name = "medication_assistants", joinColumns = @JoinColumn(name = "medication_management_id"))
    @Enumerated(EnumType.STRING)
    private List<MedicationAssistant> medicationAssistants;

    public static MedicationManagement initializeDefault() {
        MedicationManagement medicationManagement = new MedicationManagement();
        medicationManagement.isAlone = false;
        medicationManagement.houseMateNote = "";
        medicationManagement.medicationAssistants = List.of();
        return medicationManagement;
    }

    public static MedicationManagement copy(MedicationManagement medicationManagement) {
        MedicationManagement copiedMedicationManagement = new MedicationManagement();
        copiedMedicationManagement.isAlone = medicationManagement.isAlone;
        copiedMedicationManagement.houseMateNote = medicationManagement.houseMateNote;
        copiedMedicationManagement.medicationAssistants = List.copyOf(medicationManagement.medicationAssistants);
        return copiedMedicationManagement;
    }

    public void update(MedicationManagementDTO medicationManagementDTO) {
        if (Objects.isNull(medicationManagementDTO)) {
            return;
        }
        this.isAlone = Objects.requireNonNullElse(medicationManagementDTO.isAlone(), this.isAlone);
        this.houseMateNote = Objects.requireNonNullElse(medicationManagementDTO.houseMateNote(), this.houseMateNote);
        this.medicationAssistants = Objects.requireNonNullElse(medicationManagementDTO.medicationAssistants(),
                this.medicationAssistants);
    }
}
