package com.springboot.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "counseling_sessions")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSchedule"})
@ToString(callSuper = true, exclude = {"counselSchedule"})
public class CounselingSession extends BaseEntity {

    // 상담 일정과의 1대1 관계 매핑
    @OneToOne
    @JoinColumn(name = "counsel_schedule_id", nullable = false)
    private CounselSchedule counselSchedule;

    // 엔티티가 저장되기 전에 호출되어 ID 설정
    @PrePersist
    protected void onCreate() {
        super.onCreate();
    }

}
