package com.springboot.api.counselcard.dto.information.independentlife;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.EvacuationType;
import java.util.Set;

public record EvacuationDTO(
    @ValidEnum(enumClass = EvacuationType.class)
    Set<EvacuationType> evacuations,

    String evacuationNote) {
}
