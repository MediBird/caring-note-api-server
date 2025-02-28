package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

public record AddCounselorRes(String id, RoleType roleType) {
}
