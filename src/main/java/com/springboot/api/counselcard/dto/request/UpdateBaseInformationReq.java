package com.springboot.api.counselcard.dto.request;

import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import jakarta.validation.constraints.NotNull;


public record UpdateBaseInformationReq(
    @NotNull
    CounselPurposeAndNoteDTO counselPurposeAndNote
) {

}
