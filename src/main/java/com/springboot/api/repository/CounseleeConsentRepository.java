package com.springboot.api.repository;

import com.springboot.api.domain.CounseleeConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounseleeConsentRepository extends JpaRepository<CounseleeConsent, String> {
    Optional<CounseleeConsent> findByCounseleeId(String counseleeId);
}
