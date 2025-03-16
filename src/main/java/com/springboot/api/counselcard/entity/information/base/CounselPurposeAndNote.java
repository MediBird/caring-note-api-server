package com.springboot.api.counselcard.entity.information.base;

import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.enums.CounselPurposeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class CounselPurposeAndNote {

    private String SignificantNote;
    private String MedicationNote;
    @Enumerated(EnumType.STRING)
    private Set<CounselPurposeType> counselPurpose;

    public static CounselPurposeAndNote from(CounselPurposeAndNoteDTO counselPurposeAndNoteDTO) {
         CounselPurposeAndNote counselPurposeAndNote = new CounselPurposeAndNote();
         counselPurposeAndNote.SignificantNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.significantNote(), "");
         counselPurposeAndNote.MedicationNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.medicationNote(), "");
         counselPurposeAndNote.counselPurpose = Objects.requireNonNullElse(counselPurposeAndNoteDTO.counselPurpose(), Set.of());
         return counselPurposeAndNote;
    }

    public void update(CounselPurposeAndNoteDTO counselPurposeAndNoteDTO) {
        this.SignificantNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.significantNote(), this.SignificantNote);
        this.MedicationNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.medicationNote(), this.MedicationNote);
        this.counselPurpose = Objects.requireNonNullElse(counselPurposeAndNoteDTO.counselPurpose(), this.counselPurpose);
    }

}
