package com.springboot.api.domain;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "counseling_sessions")
@Data
public class CounselingSession {

    @Id
    @Column(length = 26)
    private String id;

    // 상담 일정과의 1대1 관계 매핑
    @OneToOne
    @JoinColumn(name = "counsel_schedule_id", nullable = false)
    private CounselSchedule counselSchedule;


    // 엔티티가 저장되기 전에 호출되어 ID 설정
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
    }

 
}

