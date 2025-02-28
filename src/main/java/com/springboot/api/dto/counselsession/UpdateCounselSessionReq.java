package com.springboot.api.dto.counselsession;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.ScheduleStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCounselSessionReq {

        @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
        @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
        private String counselSessionId;

        @NotBlank(message = "내담자 ID는 필수 입력값입니다")
        @Size(min = 26, max = 26, message = "내담자 ID는 26자여야 합니다")
        private String counseleeId;

        @NotBlank(message = "상담자 ID는 필수 입력값입니다")
        @Size(min = 26, max = 26, message = "상담자 ID는 26자여야 합니다")
        private String counselorId;

        @Schema(description = "상담 일정 날짜 및 시간", example = "2024-11-23 14:30")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        @NotBlank(message = "상담 일정 날짜 및 시간은 필수 입력값입니다")
        private String scheduledStartDateTime;

        @Schema(description = "상담 상태(SCHEDULED, COMPLETED, CANCELED)", example = "SCHEDULED")
        @ValidEnum(enumClass = ScheduleStatus.class)
        private ScheduleStatus status;
}
