package com.springboot.api.dto.counselee;

import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import java.time.LocalDate;

public record SelectByCounselSessionIdRes(
        String counseleeId
        , String name
        , int age
        , String dateOfBirth
        , GenderType gender
        , String address
        , HealthInsuranceType healthInsuranceType
        , int counsellingCount
        , LocalDate LastCounselingDate
) { }
