package com.springboot.api.counselsession.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.enums.ScheduleStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "counsel_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counselor_id", "scheduled_start_datetime" }),
        @UniqueConstraint(columnNames = { "counselee_id", "scheduled_start_datetime" })
})
@Getter
// TODO Update API deprecated 될 경우 Setter 삭제 필요
@Setter
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

    // TODO 연관관계 검토
    @OneToOne(mappedBy = "counselSession", cascade = CascadeType.ALL)
    private CounselCard counselCard;

    public CounselSession(Counselee counselee, LocalDateTime scheduledStartDateTime) {
        this.counselee = Objects.requireNonNull(counselee, "상담 세션의 내담자는 필수 입력 항목입니다.");
        this.scheduledStartDateTime = Objects.requireNonNull(scheduledStartDateTime, "상담 세션의 시작 시간은 필수 입력 항목입니다.");
        this.status = ScheduleStatus.SCHEDULED;
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
        this.counselor = Objects.requireNonNullElse(counselor, this.counselor);
    }

    public void updateStatus(ScheduleStatus status) {
        if (this.status.equals(ScheduleStatus.COMPLETED)) {
            throw new IllegalArgumentException("이미 완료된 상담 세션의 상태는 변경할 수 없습니다.");
        }
        this.status = Objects.requireNonNullElse(status, this.status);

        if (this.status.equals(ScheduleStatus.COMPLETED)) {
            this.counselee.counselSessionComplete(this.scheduledStartDateTime.toLocalDate());
        }
    }

}
