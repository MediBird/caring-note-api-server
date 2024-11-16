package com.springboot.api.domain;

import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "counsel_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"counselor_id", "scheduled_start_datetime", "scheduled_end_datetime"}),
        @UniqueConstraint(columnNames = {"counselee_id", "scheduled_start_datetime", "scheduled_end_datetime"})
})
@Data
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

    // 정확한 상담 시작 날짜 및 시간
    @Column(name = "scheduled_start_datetime", nullable = false)
    @NotNull(message = "상담 시작 시간은 필수 입력 항목입니다.")
    private LocalDateTime scheduledStartDateTime;

    // 정확한 상담 종료 날짜 및 시간
    @Column(name = "scheduled_end_datetime", nullable = false)
    @NotNull(message = "상담 종료 시간은 필수 입력 항목입니다.")
    private LocalDateTime scheduledEndDateTime;

    // 상담 상태 (예: 예정, 완료, 취소 등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.status == null) {
            this.status = ScheduleStatus.SCHEDULED;
        }
    }

}
