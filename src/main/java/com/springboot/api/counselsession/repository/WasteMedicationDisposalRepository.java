package com.springboot.api.counselsession.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.WasteMedicationDisposal;

public interface WasteMedicationDisposalRepository extends JpaRepository<WasteMedicationDisposal, String> {

    Optional<WasteMedicationDisposal> findByCounselSessionId(String counselSessionId);

    void deleteByCounselSessionId(String counselSessionId);
}
