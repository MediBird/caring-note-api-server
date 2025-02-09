package com.springboot.api.repository;

import com.springboot.api.domain.AICounselSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AICounselSummaryRepository extends JpaRepository<AICounselSummary, String> {

    Optional<AICounselSummary> findByCounselSessionId(String counselSessionId);

}
