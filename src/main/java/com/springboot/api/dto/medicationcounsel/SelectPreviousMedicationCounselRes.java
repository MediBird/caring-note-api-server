package com.springboot.api.dto.medicationcounsel;

import java.util.List;

public record SelectPreviousMedicationCounselRes(
                String previousCounselSessionId, List<MedicationCounselHighlightDTO> counselRecordHighlights,
                String counselNoteSummary) {
}
