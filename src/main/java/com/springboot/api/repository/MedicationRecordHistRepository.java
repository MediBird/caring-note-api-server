package com.springboot.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.MedicationRecordHist;

public interface MedicationRecordHistRepository extends JpaRepository<MedicationRecordHist, String> {

    List<MedicationRecordHist> findByCounselSessionId(String counselSessionId);

    void deleteByCounselSessionId(String counselSessionId);
}
