package com.springboot.api.counselcard.dto.information.base.item;

import java.util.List;

import lombok.Builder;

@Builder
public record CounselPurposeAndNoteDTO(
        List<String> counselPurpose, String SignificantNote, String MedicationNote) {
}
