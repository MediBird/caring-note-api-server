package com.springboot.api.dto.counselee;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeleteCounseleeBatchReq {
    @NotBlank
    private String counseleeId;
}
