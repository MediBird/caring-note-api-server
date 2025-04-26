package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

public record UpdateCounselorRes(
    String id,
    String name,
    RoleType roleType) {

}