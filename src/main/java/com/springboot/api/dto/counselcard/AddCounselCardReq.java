package com.springboot.api.dto.counselcard;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.CardRecordStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounselCardReq {

    @NotBlank
    private String counselSessionId;

    @ValidEnum(enumClass = CardRecordStatus.class)
    @Schema(description = "상담카드기록상태(RECORDING, RECORDED", example = "RECORDING")
    private CardRecordStatus cardRecordStatus;

    private JsonNode baseInformation;

    private JsonNode healthInformation;

    private JsonNode livingInformation;

    private JsonNode independentLifeInformation;

}
