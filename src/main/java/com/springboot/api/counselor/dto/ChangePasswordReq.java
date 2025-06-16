package com.springboot.api.counselor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordReq {

    /**
     * 새 비밀번호
     */
    @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
} 