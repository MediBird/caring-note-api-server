package com.springboot.api.domain;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;

    protected void onCreate() {
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
    }

}

