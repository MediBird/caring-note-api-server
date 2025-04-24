package com.springboot.api.counselor.dto;

import com.springboot.api.counselor.entity.Counselor;
import com.springboot.enums.RoleType;
import java.time.LocalDate;

public record CounselorInfoListRes(String id, String name, RoleType roleType, String username, String phoneNumber,
                                   LocalDate registrationDate, String description) {

    public static CounselorInfoListRes from(Counselor counselor) {
        return new CounselorInfoListRes(
            counselor.getId(),
            counselor.getName(),
            counselor.getRoleType(),
            counselor.getUsername(),
            counselor.getPhoneNumber(),
            counselor.getRegistrationDate(),
            counselor.getDescription() != null ? counselor.getDescription() : ""
        );
    }
}
