package com.springboot.api.dto.counselcard.information.base.item;

import java.time.LocalDate;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.enums.HealthInsuranceType;

import lombok.Builder;

||||||| parent of 2e8ea59 (refactor: :recycle: modify DTOs of the counsel card)
=======
import com.fasterxml.jackson.annotation.JsonFormat;
import com.springboot.enums.MedicalCoverageType;

import lombok.Builder;

>>>>>>> 2e8ea59 (refactor: :recycle: modify DTOs of the counsel card)
@Builder
public record BaseInfoDTO(
        String counseleeId
        ,String name
<<<<<<< HEAD
        ,@JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate
        ,String counselSessionOrder
        ,@JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastCounselDate
        ,HealthInsuranceType healthInsuranceType
||||||| parent of 2e8ea59 (refactor: :recycle: modify DTOs of the counsel card)
        , @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate
        , String counselSessionOrder
        , @JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastCounselDate
=======
        ,@JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate
        ,String counselSessionOrder
        ,@JsonFormat(pattern = "yyyy-MM-dd") LocalDate lastCounselDate
        ,MedicalCoverageType medicalCoverage
>>>>>>> 2e8ea59 (refactor: :recycle: modify DTOs of the counsel card)
){}
