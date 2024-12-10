package com.springboot.api.repository;

import com.springboot.api.domain.MedicationCounsel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicationCounselRepository extends JpaRepository<MedicationCounsel, String> {

    Optional<MedicationCounsel> findByCounselSessionId(String counselSessionId);
}
