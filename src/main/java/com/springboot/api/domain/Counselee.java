package com.springboot.api.domain;
import java.time.LocalDate;
import java.util.List;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "counselees")
@Data
public class Counselee {

    @Id
    @Column(length = 26)
    private String id;

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
    private List<CounselingSession> counselingSessions;

    @OneToMany(mappedBy = "counselee", cascade = CascadeType.ALL)
    private List<MedicationRecord> medicationRecords;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
        registrationDate = LocalDate.now();
    }
}
