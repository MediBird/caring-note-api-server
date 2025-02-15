package com.springboot.api.dto.counselcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCounselCardReq {

    @NotBlank(message = "상담카드 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담카드 ID는 26자여야 합니다")
    private String counselCardId;
}
