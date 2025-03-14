package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.enums.MedicationAssistant;
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
    private String houseMateNote;
    @Enumerated(EnumType.STRING)
    private Set<MedicationAssistant> medicationAssistants;

    public static MedicationManagement from(MedicationManagementDTO medicationManagementDTO) {
        MedicationManagement medicationManagement = new MedicationManagement();
        medicationManagement.houseMateNote = Objects.requireNonNullElse(medicationManagementDTO.houseMateNote(), "");
        medicationManagement.medicationAssistants = Objects.requireNonNullElse(medicationManagementDTO.medicationAssistants(), Set.of());
        return medicationManagement;
    }

    public void update(MedicationManagementDTO medicationManagementDTO) {
        this.houseMateNote = Objects.requireNonNullElse(medicationManagementDTO.houseMateNote(), this.houseMateNote);
        this.medicationAssistants = Objects.requireNonNullElse(medicationManagementDTO.medicationAssistants(), this.medicationAssistants);
    }
}
