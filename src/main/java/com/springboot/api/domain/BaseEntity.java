package com.springboot.api.domain;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@MappedSuperclass
@Data
@EqualsAndHashCode
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;


    @Column(updatable = false)
    private LocalDate createdDate;

    @Column
    private LocalDate updatedDate;



    protected void onCreate() {
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
        createdDate = LocalDate.now();
        updatedDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDate.now();
    }

}

