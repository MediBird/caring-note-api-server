package com.springboot.api.dto.counselor;

import com.springboot.enums.RoleType;

public record LoginCounselorRes(String counselorId, RoleType roleType){}
