package com.springboot.api.dto.counselsession;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounselSessionReq {

    @NotBlank
    private String counseleeId;

    @Schema(description = "상담 일정 날짜 및 시간",
            example = "2024-11-23 14:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotBlank
    private String scheduledStartDateTime;

    @Schema(description = "상담 상태(SCHEDULED, COMPLETED, CANCELED)",
            example = "SCHEDULED")

    @ValidEnum(enumClass = ScheduleStatus.class)
    private ScheduleStatus status;

}
