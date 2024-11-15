package com.springboot.api.domain;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;

    // 엔티티가 저장되기 전에 호출되어 ID 설정
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
    }

}

