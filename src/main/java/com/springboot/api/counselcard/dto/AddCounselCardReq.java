package com.springboot.api.counselcard.dto;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;
import com.springboot.enums.CardRecordStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounselCardReq {

    @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    private String counselSessionId;

    @ValidEnum(enumClass = CardRecordStatus.class)
    @Schema(description = "상담카드기록상태(RECORDING, RECORDED", example = "RECORDING")
    private CardRecordStatus cardRecordStatus;

    private BaseInformationDTO baseInformation;

    private HealthInformationDTO healthInformation;

    private LivingInformationDTO livingInformation;

    private IndependentLifeInformationDTO independentLifeInformation;

}
