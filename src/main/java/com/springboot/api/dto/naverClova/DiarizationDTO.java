package com.springboot.api.dto.naverClova;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiarizationDTO {
    @Builder.Default
    private final Boolean enable = Boolean.FALSE;
    private Integer speakerCountMin;
    private Integer speakerCountMax;

}
