package com.springboot.api.counselcard.dto;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;
import com.springboot.enums.CardRecordStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCounselCardReq {

    @NotBlank
    private String counselCardId;

    private BaseInformationDTO baseInformation;

    private HealthInformationDTO healthInformation;

    private LivingInformationDTO livingInformation;

    private IndependentLifeInformationDTO independentLifeInformation;

    @ValidEnum(enumClass = CardRecordStatus.class)
    @Schema(description = "상담카드기록상태(RECORDING, RECORDED", example = "RECORDING")
    private CardRecordStatus cardRecordStatus;
}
