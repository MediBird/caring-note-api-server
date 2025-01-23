package com.springboot.api.dto.counselee;

import java.time.LocalDate;

import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class SelectCounseleeRes {
    private String id;
    private String name;
    private int age;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private GenderType gender;
    private String address;
    private String affiliatedWelfareInstitution;
    private HealthInsuranceType healthInsuranceType;
    private int counselCount;
    private LocalDate lastCounselDate;
    private LocalDate registrationDate;
    private String careManagerName;
    private String note;
    private boolean disability;
}

