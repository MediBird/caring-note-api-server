package com.springboot.api.counselsession.dto.naverClova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiarizationDTO {
    @Builder.Default
    private final Boolean enable = Boolean.TRUE;
    private Integer speakerCountMin;
    private Integer speakerCountMax;

}
