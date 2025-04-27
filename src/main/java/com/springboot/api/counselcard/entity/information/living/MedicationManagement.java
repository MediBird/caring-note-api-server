package com.springboot.api.counselcard.entity.information.living;

import java.util.List;
import java.util.Objects;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationManagement {

    private Boolean isAlone;

    private String houseMateNote;

    @ElementCollection
    @CollectionTable(name = "medication_assistant_details",
                joinColumns = @JoinColumn(name = "medication_management_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (medication_management_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    private List<MedicationAssistantDetail> medicationAssistants;

    public static MedicationManagement initializeDefault() {
        return MedicationManagement.builder()
            .isAlone(null)
            .houseMateNote(null)
            .medicationAssistants(List.of())
            .build();
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

        this.isAlone = medicationManagementDTO.isAlone();
        this.houseMateNote = medicationManagementDTO.houseMateNote();
        this.medicationAssistants = medicationManagementDTO.medicationAssistants();
    }
}
