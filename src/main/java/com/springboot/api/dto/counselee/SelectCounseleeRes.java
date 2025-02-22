package com.springboot.api.dto.counselee;

import java.time.LocalDate;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.Counselee;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
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
    private boolean isDisability;

    public static SelectCounseleeRes of(Counselee counselee) {
        return SelectCounseleeRes.builder()
                .id(counselee.getId())
                .name(counselee.getName())
                .age(DateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()))
                .dateOfBirth(counselee.getDateOfBirth())
                .phoneNumber(counselee.getPhoneNumber())
                .gender(counselee.getGenderType())
                .address(counselee.getAddress())
                .affiliatedWelfareInstitution(counselee.getAffiliatedWelfareInstitution())
                .healthInsuranceType(counselee.getHealthInsuranceType())
                .counselCount(counselee.getCounselCount())
                .lastCounselDate(counselee.getLastCounselDate())
                .registrationDate(counselee.getRegistrationDate())
                .careManagerName(counselee.getCareManagerName())
                .note(counselee.getNote())
                .isDisability(counselee.getIsDisability())
                .build();
    }
}

