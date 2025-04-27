package com.springboot.api.counselcard.dto.information.independentlife;

import java.util.List;

import com.springboot.api.counselcard.entity.information.independentlife.Communication;
import com.springboot.enums.CommunicationType;
import com.springboot.enums.HearingType;
import com.springboot.enums.SightType;
import com.springboot.enums.UsingKoreanType;

public record CommunicationDTO(
    List<SightType> sights,
    List<HearingType> hearings,
    CommunicationType communications,
    List<UsingKoreanType> usingKoreans) {

    public CommunicationDTO(Communication communication) {
        this(
            communication.getSights(),
            communication.getHearings(),
            communication.getCommunications(),
            communication.getUsingKoreans()
        );
    }
}
