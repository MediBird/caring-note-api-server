package com.springboot.api.counselcard.dto.information.base;

import java.util.List;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.base.CounselPurposeAndNote;
import com.springboot.enums.CounselPurposeType;

public record CounselPurposeAndNoteDTO(
    @ValidEnum(enumClass = CounselPurposeType.class)
        List<CounselPurposeType> counselPurpose,
            String significantNote,
    String medicationNote) {

    public CounselPurposeAndNoteDTO(CounselPurposeAndNote counselPurposeAndNote) {
        this(
            counselPurposeAndNote.getCounselPurpose(),
            counselPurposeAndNote.getSignificantNote(),
            counselPurposeAndNote.getMedicationNote()
        );
    }
}
