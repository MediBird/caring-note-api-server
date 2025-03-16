package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.independentlife.Evacuation;
import com.springboot.enums.EvacuationType;
import java.util.Set;

public record EvacuationDTO(
    @ValidEnum(enumClass = EvacuationType.class)
    Set<EvacuationType> evacuations,

    String evacuationNote) {

    public EvacuationDTO(Evacuation evacuation) {
        this(
            evacuation.getEvacuations(),
            evacuation.getEvacuationNote()
        );
    }
}
