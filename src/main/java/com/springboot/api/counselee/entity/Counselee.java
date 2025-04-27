package com.springboot.api.counselee.entity;

import jakarta.persistence.Table;
import java.time.LocalDate;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselee.dto.AddCounseleeReq;
import com.springboot.api.counselee.dto.UpdateCounseleeReq;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "counselees")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Counselee extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;

    // 상담 횟수
    @Min(value = 0, message = "상담 횟수는 0 이상이어야 합니다.")
    private int counselCount;

    // 마지막 상담 날짜
    @Temporal(TemporalType.DATE)
    private LocalDate lastCounselDate;

    @Column(updatable = false)
    @Temporal(TemporalType.DATE)
    @SuppressWarnings("FieldMayBeFinal")
    private LocalDate registrationDate;

    @Column(name = "affiliated_welfare_institution")
    private String affiliatedWelfareInstitution;

    // 메모
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Enumerated(EnumType.STRING)
    private HealthInsuranceType healthInsuranceType;

    private String address;

    private Boolean isDisability;

    @Column(name = "care_manager_name")
    private String careManagerName;


    public Counselee(AddCounseleeReq addCounseleeReq) {
        super();
        this.name = addCounseleeReq.getName();
        this.dateOfBirth = addCounseleeReq.getDateOfBirth();
        this.phoneNumber = addCounseleeReq.getPhoneNumber();
        this.counselCount = 0;
        this.registrationDate = LocalDate.now();
        this.affiliatedWelfareInstitution = addCounseleeReq.getAffiliatedWelfareInstitution();
        this.note = addCounseleeReq.getNote();
        this.genderType = addCounseleeReq.getGenderType();
        this.healthInsuranceType = HealthInsuranceType.NON_COVERED;
        this.address = addCounseleeReq.getAddress();
        this.isDisability = addCounseleeReq.getIsDisability();
        this.careManagerName = addCounseleeReq.getCareManagerName();
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public void update(UpdateCounseleeReq updateCounseleeReq) {
        Optional.ofNullable(updateCounseleeReq.getName()).ifPresent(value -> this.name = value);
        Optional.ofNullable(updateCounseleeReq.getDateOfBirth()).ifPresent(value -> this.dateOfBirth = value);
        Optional.ofNullable(updateCounseleeReq.getPhoneNumber()).ifPresent(value -> this.phoneNumber = value);
        Optional.ofNullable(updateCounseleeReq.getLastCounselDate()).ifPresent(value -> this.lastCounselDate = value);
        Optional.ofNullable(updateCounseleeReq.getAffiliatedWelfareInstitution())
            .ifPresent(value -> this.affiliatedWelfareInstitution = value);
        Optional.ofNullable(updateCounseleeReq.getNote()).ifPresent(value -> this.note = value);
        Optional.ofNullable(updateCounseleeReq.getGenderType()).ifPresent(value -> this.genderType = value);
        Optional.ofNullable(updateCounseleeReq.getAddress()).ifPresent(value -> this.address = value);
        Optional.ofNullable(updateCounseleeReq.getIsDisability()).ifPresent(value -> this.isDisability = value);
        Optional.ofNullable(updateCounseleeReq.getCareManagerName()).ifPresent(value -> this.careManagerName = value);
    }
}
