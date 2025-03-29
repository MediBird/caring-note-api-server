package com.springboot.api.counselcard.entity.information.base;

import java.util.ArrayList;
import java.util.List;

import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.enums.CounselPurposeType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class CounselPurposeAndNote {

    private String significantNote;

    private String medicationNote;

    @ElementCollection
    @CollectionTable(name = "counsel_purpose", joinColumns = @JoinColumn(name = "counsel_purpose_and_note_id"))
    @Enumerated(EnumType.STRING)
    private List<CounselPurposeType> counselPurpose;

    public static CounselPurposeAndNote initializeDefault() {
        CounselPurposeAndNote counselPurposeAndNote = new CounselPurposeAndNote();
        counselPurposeAndNote.significantNote = "";
        counselPurposeAndNote.medicationNote = "";
        counselPurposeAndNote.counselPurpose = List.of();
        return counselPurposeAndNote;
    }

    public static CounselPurposeAndNote copy(CounselPurposeAndNote evacuation) {
        CounselPurposeAndNote copiedCounselPurposeAndNote = new CounselPurposeAndNote();
        copiedCounselPurposeAndNote.significantNote = evacuation.significantNote;
        copiedCounselPurposeAndNote.medicationNote = evacuation.medicationNote;
        copiedCounselPurposeAndNote.counselPurpose = new ArrayList<>(evacuation.counselPurpose);
        return copiedCounselPurposeAndNote;
    }

    public void update(CounselPurposeAndNoteDTO counselPurposeAndNoteDTO) {
        if(Objects.isNull(counselPurposeAndNoteDTO)) {
            return;
        }
        this.significantNote = counselPurposeAndNoteDTO.significantNote();
        this.medicationNote = counselPurposeAndNoteDTO.medicationNote();
        this.counselPurpose = counselPurposeAndNoteDTO.counselPurpose();
    }

}
