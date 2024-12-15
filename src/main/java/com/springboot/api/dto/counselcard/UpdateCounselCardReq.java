package com.springboot.api.dto.counselcard;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.CardRecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private Map<String, Object> independentLifeInformation;

    @ValidEnum(enumClass = CardRecordStatus.class)
    @Schema(description = "상담카드기록상태(RECORDING, RECORDED", example = "RECORDING")
    private CardRecordStatus cardRecordStatus;
}
