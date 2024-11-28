package com.springboot.api.domain;

import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "counsel_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"counselor_id", "scheduled_start_datetime"}),
        @UniqueConstraint(columnNames = {"counselee_id", "scheduled_start_datetime"})
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselor", "counselee"})
@ToString(callSuper = true, exclude = {"counselor", "counselee"})
public class CounselSession extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counselor_id", nullable = false)
    private Counselor counselor;

    // 내담자와의 관계 매핑 (다대일)
    @ManyToOne
    @JoinColumn(name = "counselee_id", nullable = false)
    private Counselee counselee;

    //상담 일정 날짜 및 시간
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
