package com.springboot.api.dto.counselsession;

import com.springboot.enums.ScheduleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateStatusInCounselSessionReq(
        @NotBlank String counselSessionId,
        @NotNull ScheduleStatus status) {}
