package com.springboot.api.dto.counselee;

import java.time.LocalDate;
import java.util.List;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.Counselee;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import lombok.Builder;

@Builder
public record SelectCounseleeBaseInformationByCounseleeIdRes(
                String counseleeId, String name, int age, String dateOfBirth, GenderType gender, String address,
                HealthInsuranceType healthInsuranceType, int counselCount, LocalDate lastCounselDate,
                List<String> diseases, CardRecordStatus cardRecordStatus, boolean isDisability) {

    public static SelectCounseleeBaseInformationByCounseleeIdRes of(Counselee counselee, List<String> diseases, CardRecordStatus cardRecordStatus) {
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
