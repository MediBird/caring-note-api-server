package com.springboot.api.counselcard.dto.information.independentlife;

import java.util.List;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.independentlife.Evacuation;
import com.springboot.enums.EvacuationType;

public record EvacuationDTO(
    @ValidEnum(enumClass = EvacuationType.class)
        List<EvacuationType> evacuations,
        
    String evacuationNote) {

    public EvacuationDTO(Evacuation evacuation) {
        this(
            evacuation.getEvacuations(),
            evacuation.getEvacuationNote()
        );
    }
}
