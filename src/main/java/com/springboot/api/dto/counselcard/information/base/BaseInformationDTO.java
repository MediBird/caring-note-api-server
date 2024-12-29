package com.springboot.api.dto.counselcard.information.base;

import com.springboot.api.dto.counselcard.information.base.item.BaseInfoDTO;
import com.springboot.api.dto.counselcard.information.base.item.CounselPurposeAndNoteDTO;
import lombok.Builder;

@Builder
public record BaseInformationDTO(
        String version
        , BaseInfoDTO baseInfo
        , CounselPurposeAndNoteDTO counselPurposeAndNote

        ) {}
