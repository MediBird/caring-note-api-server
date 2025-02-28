package com.springboot.api.counselsession.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.MedicationCounselHighlight;

public interface MedicationCounselHighlightRepository extends JpaRepository<MedicationCounselHighlight, String> {

}