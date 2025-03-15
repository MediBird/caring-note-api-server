package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상담사 권한 변경 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleReq {

    /**
     * 변경할 권한 유형
     */
    @NotNull(message = "권한 유형은 필수 입력 항목입니다.")
    private RoleType roleType;
}