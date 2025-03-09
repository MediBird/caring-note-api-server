package com.springboot.api.counselee.entity;

import java.time.LocalDate;
import java.util.Objects;

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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "counselees", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "date_of_birth", "phone_number" })
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false, unique = true)
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
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
        this.name = Objects.requireNonNullElse(updateCounseleeReq.getName(), this.name);
        this.phoneNumber = Objects.requireNonNullElse(updateCounseleeReq.getPhoneNumber(), this.phoneNumber);
        this.dateOfBirth = Objects.requireNonNullElse(updateCounseleeReq.getDateOfBirth(), this.dateOfBirth);
        this.genderType = Objects.requireNonNullElse(updateCounseleeReq.getGenderType(), this.genderType);
        this.address = Objects.requireNonNullElse(updateCounseleeReq.getAddress(), this.address);
        this.isDisability = Objects.requireNonNullElse(updateCounseleeReq.getIsDisability(), this.isDisability);
        this.note = Objects.requireNonNullElse(updateCounseleeReq.getNote(), this.note);
        this.careManagerName = Objects.requireNonNullElse(updateCounseleeReq.getCareManagerName(),
                this.careManagerName);
        this.affiliatedWelfareInstitution = Objects.requireNonNullElse(
                updateCounseleeReq.getAffiliatedWelfareInstitution(), this.affiliatedWelfareInstitution);
    }

    public void counselSessionComplete(LocalDate lastCounselDate) {
        this.counselCount++;
        this.lastCounselDate = lastCounselDate;
    }
}
