package com.springboot.api.counselcard.dto.request;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;
import jakarta.validation.constraints.NotNull;


public record UpdateIndependentLifeInformationReq(
    @NotNull
    CommunicationDTO communication,

    @NotNull
    EvacuationDTO evacuation,

    @NotNull
    WalkingDTO walking
) {

}
