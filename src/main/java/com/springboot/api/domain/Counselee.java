package com.springboot.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "counselees", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "dateOfBirth", "phoneNumber"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselingSessions", "medicationRecords", "counselSchedules"})
@ToString(callSuper = true, exclude = {"counselingSessions", "medicationRecords", "counselSchedules"})
public class Counselee extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;

    // 상담 횟수
    @Min(value = 0, message = "상담 횟수는 0 이상이어야 합니다.")
    private int counselingCount;

    // 마지막 상담 날짜
    private LocalDate lastCounselingDate;

    // 등록 날짜
    @Column(updatable = false)
    private LocalDate registrationDate;

    // 메모
    @Lob
    private String notes;

    @OneToMany(mappedBy = "counselee", cascade = CascadeType.ALL)
    private List<CounselSession> counselSessions;

    @OneToMany(mappedBy = "counselee", cascade = CascadeType.ALL)
    private List<MedicationRecord> medicationRecords;


    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        registrationDate = LocalDate.now();
    }
}
