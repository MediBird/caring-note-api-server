package com.springboot.api.dto.counselee;

import java.time.LocalDate;

import com.springboot.enums.GenderType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCounseleeReq {
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private GenderType genderType;
    private String address;
    private boolean isDisability;
    private String note;
    private String careManagerName;
    private String affiliatedWelfareInstitution;
}