package com.springboot.api.counselsession.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.AICounselSummary;

public interface AICounselSummaryRepository extends JpaRepository<AICounselSummary, String> {

    Optional<AICounselSummary> findByCounselSessionId(String counselSessionId);

    void deleteByCounselSessionId(String counselSessionId);

}
