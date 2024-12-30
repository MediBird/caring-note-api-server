package com.springboot.api.dto.counselcard.information.independentlife.item;

import lombok.Builder;

import java.util.List;

@Builder
public record CommunicationDTO(
        List<String> sights
        ,List<String> hearings
        , List<String> communications
        , List<String> usingKoreans
){
}
