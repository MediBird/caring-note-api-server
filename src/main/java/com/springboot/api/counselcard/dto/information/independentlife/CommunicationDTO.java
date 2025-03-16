package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.independentlife.Communication;
import com.springboot.enums.CommunicationType;
import com.springboot.enums.HearingType;
import com.springboot.enums.SightType;
import com.springboot.enums.UsingKoreanType;
import java.util.List;

public record CommunicationDTO(
    @ValidEnum(enumClass = SightType.class) List<SightType> sights,
    @ValidEnum(enumClass = HearingType.class) List<HearingType> hearings,
    @ValidEnum(enumClass = CommunicationType.class) CommunicationType communications,
    @ValidEnum(enumClass = UsingKoreanType.class) List<UsingKoreanType> usingKoreans) {

    public CommunicationDTO(Communication communication) {
        this(
            communication.getSights(),
            communication.getHearings(),
            communication.getCommunications(),
            communication.getUsingKoreans()
        );
    }
}
