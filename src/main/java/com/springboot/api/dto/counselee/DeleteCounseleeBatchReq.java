package com.springboot.api.dto.counselee;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCounseleeBatchReq {
    @NotBlank
    private String counseleeId;
}
