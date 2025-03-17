package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.base.BaseInfoDTO;
import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.enums.CardRecordStatus;


public record CounselCardBaseInformationRes(

    BaseInfoDTO baseInfo,

    CardRecordStatus cardRecordStatus,

    CounselPurposeAndNoteDTO counselPurposeAndNote
) {

    public CounselCardBaseInformationRes(CounselCard counselCard) {
        this(
            new BaseInfoDTO(counselCard.getCounselSession().getCounselee()),
            counselCard.getCardRecordStatus(),
            new CounselPurposeAndNoteDTO(counselCard.getCounselPurposeAndNote())
        );
    }
}
