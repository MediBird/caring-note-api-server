package com.springboot.api.dto.counselor;

import com.springboot.enums.RoleType;

public record AddCounselorRes(String id, RoleType roleType) {
}
