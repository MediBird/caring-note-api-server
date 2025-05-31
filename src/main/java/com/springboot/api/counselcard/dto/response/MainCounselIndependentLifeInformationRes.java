package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;

public record MainCounselIndependentLifeInformationRes(
    MainCounselRecord<CommunicationDTO> communication,
    MainCounselRecord<EvacuationDTO> evacuation,
    MainCounselRecord<WalkingDTO> walking
) {

}


