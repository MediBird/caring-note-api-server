package com.springboot.api.medication.repository;

import com.springboot.api.medication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, String>, MedicationRepositoryCustom {

}
