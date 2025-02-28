package com.springboot.api.counselsession.dto.medicationcounsel;

import java.util.List;

public record SelectMedicationCounselRes(
        String medicationCounselId, String counselRecord,
        List<MedicationCounselHighlightDTO> counselRecordHighlights) {
}
