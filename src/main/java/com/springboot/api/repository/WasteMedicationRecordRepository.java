package com.springboot.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.WasteMedicationRecord;

public interface WasteMedicationRecordRepository extends JpaRepository<WasteMedicationRecord, Long> {

    List<WasteMedicationRecord> findByCounselSessionId(String counselSessionId);

    WasteMedicationRecord findByCounselSessionIdAndMedicationId(String counselSessionId, String medicationId);

}
