package com.springboot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.MedicationRecord;

public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, String> {

}
