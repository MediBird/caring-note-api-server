package com.springboot.api.counselsession.dto.medicationcounsel;

import java.util.List;

public record SelectPreviousMedicationCounselRes(
        String previousCounselSessionId, List<MedicationCounselHighlightDTO> counselRecordHighlights,
        String counselNoteSummary) {
}
