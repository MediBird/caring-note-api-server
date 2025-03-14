package com.springboot.api.counselee.dto;

import com.springboot.enums.DiseaseType;
import java.time.LocalDate;
import java.util.List;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import java.util.Set;
import lombok.Builder;

@Builder
public record SelectCounseleeBaseInformationByCounseleeIdRes(
    String counseleeId, String name, int age, String dateOfBirth, GenderType gender, String address,
    HealthInsuranceType healthInsuranceType, int counselCount, LocalDate lastCounselDate,
    Set<DiseaseType> diseases, CardRecordStatus cardRecordStatus, boolean isDisability) {

    public static SelectCounseleeBaseInformationByCounseleeIdRes from(Counselee counselee, Set<DiseaseType> diseases,
            CardRecordStatus cardRecordStatus) {
        return SelectCounseleeBaseInformationByCounseleeIdRes.builder()
                .counseleeId(counselee.getId())
                .name(counselee.getName())
                .age(DateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()))
                .dateOfBirth(counselee.getDateOfBirth().toString())
                .gender(counselee.getGenderType())
                .address(counselee.getAddress())
                .healthInsuranceType(counselee.getHealthInsuranceType())
                .counselCount(counselee.getCounselCount())
                .lastCounselDate(counselee.getLastCounselDate())
                .diseases(diseases)
                .cardRecordStatus(cardRecordStatus)
                .isDisability(counselee.getIsDisability())
                .build();
    }
}
