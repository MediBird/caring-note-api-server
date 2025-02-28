package com.springboot.api.dto.counselcard.information.independentlife;

import com.springboot.api.dto.counselcard.information.independentlife.item.CommunicationDTO;
import com.springboot.api.dto.counselcard.information.independentlife.item.EvacuationDTO;
import com.springboot.api.dto.counselcard.information.independentlife.item.WalkingDTO;

import lombok.Builder;

@Builder
public record IndependentLifeInformationDTO(
                String version, WalkingDTO walking, EvacuationDTO evacuation, CommunicationDTO communication) {
}
