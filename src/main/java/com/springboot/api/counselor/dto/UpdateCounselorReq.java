package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCounselorReq {

    private String name;
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;
    private String description;
    private RoleType roleType;
}