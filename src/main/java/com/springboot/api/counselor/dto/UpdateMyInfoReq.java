package com.springboot.api.counselor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 내 정보 업데이트 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMyInfoReq {

    /**
     * 이름
     */
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    /**
     * 전화번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;
} 