package com.springboot.api.dto.counselcard.information.base.item;

import lombok.Builder;

@Builder
public record CounselPurposeAndNoteDTO(
        String counselPurpose
        ,String SignificantNote
        , String MedicationNote
){}
