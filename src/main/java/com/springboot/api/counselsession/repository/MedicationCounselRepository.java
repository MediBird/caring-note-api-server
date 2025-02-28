package com.springboot.api.counselsession.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.MedicationCounsel;

public interface MedicationCounselRepository extends JpaRepository<MedicationCounsel, String> {

    Optional<MedicationCounsel> findByCounselSessionId(String counselSessionId);
}
