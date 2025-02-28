package com.springboot.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.MedicationCounsel;

public interface MedicationCounselRepository extends JpaRepository<MedicationCounsel, String> {

    Optional<MedicationCounsel> findByCounselSessionId(String counselSessionId);
}
