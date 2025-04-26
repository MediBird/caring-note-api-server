package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectCounselorRes {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private RoleType roleType;
    private int medicationCounselingCount;
    private int counseledCounseleeCount;
    private int participationDays;
}