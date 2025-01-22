package com.springboot.api.dto.counselee;

import java.time.LocalDate;

import com.springboot.enums.GenderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddAndUpdateCounseleeReq {
    private String counseleeId;
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
