package com.springboot.api.counselee.dto;

import java.time.LocalDate;

import com.springboot.api.common.annotation.ValidNullableEnum;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCounseleeReq {

    @NotBlank(message = "내담자 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "내담자 ID는 26자여야 합니다")
    private String counseleeId;

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    private String name;

    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String phoneNumber;

    @PastOrPresent(message = "최근 상담일은 미래일 수 없습니다.")
    private LocalDate lastCounselDate;

    @Size(max = 100, message = "복지기관명은 100자를 초과할 수 없습니다")
    private String affiliatedWelfareInstitution;

    @Size(max = 1000, message = "비고는 1000자를 초과할 수 없습니다")
    private String note;

    @ValidNullableEnum(enumClass = GenderType.class)
    private GenderType genderType;

    @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
    private String address;

    private Boolean isDisability;

    @Size(max = 50, message = "담당자 이름은 50자를 초과할 수 없습니다")
    private String careManagerName;

    @ValidNullableEnum(enumClass = HealthInsuranceType.class)
    private HealthInsuranceType healthInsuranceType;
}
