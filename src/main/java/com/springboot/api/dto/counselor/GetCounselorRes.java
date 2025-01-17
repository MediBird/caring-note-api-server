package com.springboot.api.dto.counselor;

import com.springboot.enums.RoleType;

public record GetCounselorRes(
        String id,
        String name,
        String email,
        String phoneNumber,
        RoleType roleType,
        Integer medicationCounselingCount,
        Integer counseledCounseleeCount,
        Integer participationDays) {

}
