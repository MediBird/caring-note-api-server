package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;
import com.springboot.api.counselcard.entity.CounselCard;


public record CounselCardIndependentLifeInformationRes(
    CommunicationDTO communication,
    EvacuationDTO evacuation,
    WalkingDTO walking
) {

    public CounselCardIndependentLifeInformationRes(CounselCard counselCard) {
        this(
            new CommunicationDTO(counselCard.getCommunication()),
            new EvacuationDTO(counselCard.getEvacuation()),
            new WalkingDTO(counselCard.getWalking())
        );
    }
}
