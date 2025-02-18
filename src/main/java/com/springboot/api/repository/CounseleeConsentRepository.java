package com.springboot.api.repository;

import com.springboot.api.domain.CounseleeConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounseleeConsentRepository extends JpaRepository<CounseleeConsent, String> {

    Optional<CounseleeConsent> findByCounselSessionId(String counselSessionId);

    Optional<CounseleeConsent> findByCounselSessionIdAndCounseleeId(String counselSessionId, String CounseleeId);


}
