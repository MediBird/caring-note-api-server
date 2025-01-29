package com.springboot.api.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.springboot.enums.ScheduleStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "counsel_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counselor_id", "scheduled_start_datetime" }),
        @UniqueConstraint(columnNames = { "counselee_id", "scheduled_start_datetime" })
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselor", "counselee" })
@ToString(callSuper = true, exclude = { "counselor", "counselee" })
public class CounselSession extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;

    // 내담자와의 관계 매핑 (다대일)
    @ManyToOne
    @JoinColumn(name = "counselee_id")
    private Counselee counselee;

    // 상담 일정 날짜 및 시간
    @Column(name = "scheduled_start_datetime", nullable = false)
    @NotNull(message = "상담 시작 시간은 필수 입력 항목입니다.")
    private LocalDateTime scheduledStartDateTime;

    // 정확한 상담 종료 날짜 및 시간
    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;

    // 정확한 상담 종료 날짜 및 시간
    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;

    // 상담 상태 (예: 예정, 완료, 취소 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @OneToOne(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private CounselCard counselCard;

    @OneToMany(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private List<MedicationRecordHist> medicationRecordHists;

    @OneToOne(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private MedicationCounsel medicationCounsel;

    @OneToOne(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private WasteMedicationDisposal wasteMedicationDisposal;

    @OneToMany(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private List<WasteMedicationRecord> wasteMedicationRecords;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.status == null) {
            this.status = ScheduleStatus.SCHEDULED;
        }
    }

}
