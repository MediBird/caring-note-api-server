package com.springboot.api.counselor.dto;

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
