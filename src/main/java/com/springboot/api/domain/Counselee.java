package com.springboot.api.domain;

import java.time.LocalDate;
import java.util.List;

import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "counselees", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "date_of_birth", "phone_number" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSessions", "medicationRecords", })
@ToString(callSuper = true, exclude = { "counselSessions", "medicationRecords" })
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
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
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

    private boolean isDisability;

    @Column(name = "care_manager_name")
    private String careManagerName;

    @OneToMany(mappedBy = "counselee", cascade = CascadeType.ALL)
    private List<CounselSession> counselSessions;

    @OneToMany(mappedBy = "counselee", cascade = CascadeType.ALL)
    private List<MedicationRecord> medicationRecords;

    @OneToOne(mappedBy = "counselee", cascade = CascadeType.ALL)
    private CounseleeConsent counseleeConsent;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
