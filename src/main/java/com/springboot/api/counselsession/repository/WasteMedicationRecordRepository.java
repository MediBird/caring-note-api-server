package com.springboot.api.counselsession.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.WasteMedicationRecord;

public interface WasteMedicationRecordRepository extends JpaRepository<WasteMedicationRecord, String> {

    List<WasteMedicationRecord> findByCounselSessionId(String counselSessionId);

    WasteMedicationRecord findByCounselSessionIdAndMedicationId(String counselSessionId, String medicationId);

}
