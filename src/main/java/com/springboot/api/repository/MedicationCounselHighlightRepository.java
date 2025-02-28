package com.springboot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.MedicationCounselHighlight;

public interface MedicationCounselHighlightRepository extends JpaRepository<MedicationCounselHighlight, String> {

}