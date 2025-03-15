package com.springboot.api.counselor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 초기화 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReq {

    /**
     * 새 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;

    /**
     * 임시 비밀번호 여부
     * true인 경우 사용자는 다음 로그인 시 비밀번호 변경 필요
     */
    private boolean temporary;
}