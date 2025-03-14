package com.springboot.api.counselcard.dto.information.base;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.base.CounselPurposeAndNote;
import com.springboot.enums.CounselPurposeType;

import java.util.Set;
import javax.validation.Valid;
import lombok.Builder;

public record CounselPurposeAndNoteDTO(
    @ValidEnum(enumClass = CounselPurposeType.class)
    Set<CounselPurposeType> counselPurpose,
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
