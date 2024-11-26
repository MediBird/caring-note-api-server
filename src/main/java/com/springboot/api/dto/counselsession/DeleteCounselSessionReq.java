package com.springboot.api.dto.counselsession;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteCounselSessionReq {
    @NotBlank
    private String id;
}
