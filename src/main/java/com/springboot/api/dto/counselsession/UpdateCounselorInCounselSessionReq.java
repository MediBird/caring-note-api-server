package com.springboot.api.dto.counselsession;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateCounselorInCounselSessionReq(
        @NotBlank String counselSessionId
        , String counselorId
){}
