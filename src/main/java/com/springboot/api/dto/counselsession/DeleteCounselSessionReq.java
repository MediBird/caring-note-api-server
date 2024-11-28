package com.springboot.api.dto.counselsession;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCounselSessionReq {
    @NotBlank
    private String counselSessionId;
}
