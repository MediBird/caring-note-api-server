package com.springboot.api.counselsession.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.MedicationRecord;

public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, String> {

}
