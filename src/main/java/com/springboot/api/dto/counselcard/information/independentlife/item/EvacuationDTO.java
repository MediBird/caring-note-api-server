package com.springboot.api.dto.counselcard.information.independentlife.item;

import lombok.Builder;

import java.util.List;

@Builder
public record EvacuationDTO(
        List<String> evacuationMethods
        ,String etcNote
){
}
