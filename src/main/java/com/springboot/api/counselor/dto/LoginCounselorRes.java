package com.springboot.api.counselor.dto;

import com.springboot.enums.RoleType;

public record LoginCounselorRes(String counselorId, RoleType roleType) {

}
