package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCounselorReq {
    private String name;
    private String phoneNumber;
    private RoleType roleType;
}