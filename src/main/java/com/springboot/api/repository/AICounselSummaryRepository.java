package com.springboot.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.AICounselSummary;

public interface AICounselSummaryRepository extends JpaRepository<AICounselSummary, String> {

    Optional<AICounselSummary> findByCounselSessionId(String counselSessionId);

    void deleteByCounselSessionId(String counselSessionId);

}
