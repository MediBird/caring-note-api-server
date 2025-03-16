package com.springboot.api.counselcard.entity.information.base;

import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.enums.CounselPurposeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class CounselPurposeAndNote {

    @Column(nullable = false)
    private String SignificantNote;

    @Column(nullable = false)
    private String MedicationNote;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<CounselPurposeType> counselPurpose;

    public static CounselPurposeAndNote initializeDefault() {
        CounselPurposeAndNote counselPurposeAndNote = new CounselPurposeAndNote();
        counselPurposeAndNote.SignificantNote = "";
        counselPurposeAndNote.MedicationNote = "";
        counselPurposeAndNote.counselPurpose = Set.of();
        return counselPurposeAndNote;
    }

    public static CounselPurposeAndNote copy(CounselPurposeAndNote evacuation) {
        CounselPurposeAndNote copiedCounselPurposeAndNote = new CounselPurposeAndNote();
        copiedCounselPurposeAndNote.SignificantNote = evacuation.SignificantNote;
        copiedCounselPurposeAndNote.MedicationNote = evacuation.MedicationNote;
        copiedCounselPurposeAndNote.counselPurpose = new HashSet<>(evacuation.counselPurpose);
        return copiedCounselPurposeAndNote;
    }

    public void update(CounselPurposeAndNoteDTO counselPurposeAndNoteDTO) {
        this.SignificantNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.significantNote(), this.SignificantNote);
        this.MedicationNote = Objects.requireNonNullElse(counselPurposeAndNoteDTO.medicationNote(), this.MedicationNote);
        this.counselPurpose = Objects.requireNonNullElse(counselPurposeAndNoteDTO.counselPurpose(), this.counselPurpose);
    }

}
