package com.springboot.api.dto.counselee;

import java.time.LocalDate;
import java.util.List;

import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

public record SelectCounseleeBaseInformationByCounseleeIdRes(
                String counseleeId, String name, int age, String dateOfBirth, GenderType gender, String address,
                HealthInsuranceType healthInsuranceType, int counselCount, LocalDate lastCounselDate,
                List<String> diseases, CardRecordStatus cardRecordStatus, boolean disability) {
}
