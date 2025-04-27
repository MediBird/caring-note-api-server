package com.springboot.api.counselee.dto;

import java.time.LocalDate;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SelectCounseleeRes {

    private final String id;
    private final String name;
    private final int age;
    private final LocalDate dateOfBirth;
    private final String phoneNumber;
    private final GenderType gender;
    private final String address;
    private final String affiliatedWelfareInstitution;
    private final HealthInsuranceType healthInsuranceType;
    private final int counselCount;
    private final LocalDate lastCounselDate;
    private final LocalDate registrationDate;
    private final String careManagerName;
    private final String note;
    private final boolean isDisability;

    public static SelectCounseleeRes from(Counselee counselee) {
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
