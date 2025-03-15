package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.item.CommunicationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.item.EvacuationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.item.WalkingDTO;

import lombok.Builder;

@Builder
public record IndependentLifeInformationDTO(
        String version, WalkingDTO walking, EvacuationDTO evacuation, CommunicationDTO communication) {
}
