package com.springboot.api.counselcard.dto.request;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.CardRecordStatus;

public record UpdateCounselCardStatusReq(
    @ValidEnum(enumClass = CardRecordStatus.class)
    CardRecordStatus status
) {

}
