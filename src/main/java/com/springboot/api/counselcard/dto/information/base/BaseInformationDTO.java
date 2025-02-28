package com.springboot.api.counselcard.dto.information.base;

import com.springboot.api.counselcard.dto.information.base.item.BaseInfoDTO;
import com.springboot.api.counselcard.dto.information.base.item.CounselPurposeAndNoteDTO;

import lombok.Builder;

@Builder
public record BaseInformationDTO(
        String version,
        BaseInfoDTO baseInfo,
        CounselPurposeAndNoteDTO counselPurposeAndNote) {

}
