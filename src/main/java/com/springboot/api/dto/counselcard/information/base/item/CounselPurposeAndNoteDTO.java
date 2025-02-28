package com.springboot.api.dto.counselcard.information.base.item;

import java.util.List;

import lombok.Builder;

@Builder
public record CounselPurposeAndNoteDTO(
                List<String> counselPurpose, String SignificantNote, String MedicationNote) {
}
