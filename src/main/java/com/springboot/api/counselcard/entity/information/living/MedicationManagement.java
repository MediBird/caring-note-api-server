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
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MedicationManagement {

    private Boolean isAlone;

    private String houseMateNote;

    @ElementCollection
    @CollectionTable(name = "medication_assistants",
        joinColumns = @JoinColumn(name = "medication_management_id"),
        foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (medication_management_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<MedicationAssistant> medicationAssistants;

    private String customMedicationAssistant;

    public static MedicationManagement initializeDefault() {
        return new MedicationManagement(null, null, List.of(), null);
    }

    public static MedicationManagement copy(MedicationManagement medicationManagement) {
        MedicationManagement copiedMedicationManagement = new MedicationManagement();
        copiedMedicationManagement.isAlone = medicationManagement.isAlone;
        copiedMedicationManagement.houseMateNote = medicationManagement.houseMateNote;
        copiedMedicationManagement.medicationAssistants = List.copyOf(medicationManagement.medicationAssistants);
        copiedMedicationManagement.customMedicationAssistant = medicationManagement.customMedicationAssistant;
        return copiedMedicationManagement;
    }

    public void update(MedicationManagementDTO medicationManagementDTO) {
        if (Objects.isNull(medicationManagementDTO)) {
            return;
        }

        this.isAlone = medicationManagementDTO.isAlone();
        this.houseMateNote = medicationManagementDTO.houseMateNote();
        this.medicationAssistants = medicationManagementDTO.medicationAssistants();
        if (Objects.nonNull(this.medicationAssistants)
                && !this.medicationAssistants.contains(MedicationAssistant.OTHER)) {
            this.customMedicationAssistant = null;
        } else {
            this.customMedicationAssistant = medicationManagementDTO.customMedicationAssistant();
        }
    }
}
