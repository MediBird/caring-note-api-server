package com.springboot.api.dto.counselcard;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UpdateCounselCardReq {

    @NotBlank
    private String counselCardId;

    private Map<String, Object> baseInformation;

    private Map<String, Object> healthInformation;

    private Map<String, Object> livingInformation;

    private Map<String, Object> selfReliantLivingInformation;
}