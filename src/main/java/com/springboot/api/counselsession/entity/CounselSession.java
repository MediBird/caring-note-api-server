package com.springboot.api.counselsession.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.enums.ScheduleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "counsel_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counselee_id", "scheduled_start_datetime" })
})
@SuperBuilder
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselor", "counselee" })
@ToString(callSuper = true, exclude = { "counselor", "counselee" })
public class CounselSession extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counselor_id")
    private Counselor counselor;

    // 내담자와의 관계 매핑 (다대일)
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    // 상담 회차 정보
    @Column(name = "session_number")
    private Integer sessionNumber;

    public static CounselSession createReservation(Counselee counselee, LocalDateTime scheduledStartDateTime) {
        return CounselSession.builder()
                .counselee(counselee)
                .scheduledStartDateTime(scheduledStartDateTime)
                .status(ScheduleStatus.SCHEDULED)
                .build();
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.status == null) {
            this.status = ScheduleStatus.SCHEDULED;
        }
    }

    public void updateCounselor(Counselor counselor) {
        this.counselor = counselor;
    }

    public void modifyReservation(LocalDateTime scheduledStartDateTime, Counselee counselee) {
        this.scheduledStartDateTime = Objects.requireNonNullElse(scheduledStartDateTime, this.scheduledStartDateTime);
        this.counselee = Objects.requireNonNullElse(counselee, this.counselee);
    }

    public void updateSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public void completeCounselSession() {
        if (!this.status.equals(ScheduleStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("진행 중인 상담 세션만 완료할 수 있습니다.");
        }

        this.status = ScheduleStatus.COMPLETED;
        this.endDateTime = LocalDateTime.now();
    }

    public void cancelCounselSession() {
        if (this.status.equals(ScheduleStatus.COMPLETED)) {
            throw new IllegalArgumentException("완료된 상담 세션은 취소할 수 없습니다.");
        }

        this.status = ScheduleStatus.CANCELED;
        this.startDateTime = null;
        this.endDateTime = null;
    }

    public void scheduleCounselSession() {
        if (this.status.equals(ScheduleStatus.COMPLETED)) {
            throw new IllegalArgumentException("완료된 상담 세션은 재예약할 수 없습니다.");
        }

        this.status = ScheduleStatus.SCHEDULED;
        this.startDateTime = null;
        this.endDateTime = null;
    }

    public void progressCounselSession() {
        if (!this.status.equals(ScheduleStatus.SCHEDULED)) {
            throw new IllegalArgumentException("예정된 상담 세션만 진행할 수 있습니다.");
        }

        this.status = ScheduleStatus.IN_PROGRESS;
        this.startDateTime = LocalDateTime.now();
    }
}
