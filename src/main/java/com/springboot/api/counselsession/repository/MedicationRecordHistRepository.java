package com.springboot.api.counselsession.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.MedicationRecordHist;

public interface MedicationRecordHistRepository extends JpaRepository<MedicationRecordHist, String> {

    List<MedicationRecordHist> findByCounselSessionId(String counselSessionId);

    void deleteByCounselSessionId(String counselSessionId);
}
