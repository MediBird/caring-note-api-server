package com.springboot.api.dto.counselcard;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCounselCardReq {

    @NotBlank
    private String counselCardId;
}
