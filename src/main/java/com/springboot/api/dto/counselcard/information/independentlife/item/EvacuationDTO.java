package com.springboot.api.dto.counselcard.information.independentlife.item;

import java.util.List;

import lombok.Builder;

@Builder
public record EvacuationDTO(
        List<String> evacuationMethods
        ,String etcNote
){
}
