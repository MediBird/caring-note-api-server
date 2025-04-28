package com.springboot.api.counselsession.repository;

import com.springboot.api.counselsession.entity.CounseleeConsent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounseleeConsentRepository extends JpaRepository<CounseleeConsent, String> {

    Optional<CounseleeConsent> findByCounselSessionId(String counselSessionId);

    Optional<CounseleeConsent> findByCounselSessionIdAndCounseleeId(String counselSessionId, String CounseleeId);

    Boolean existsByCounselSessionIdAndCounseleeId(String counselSessionId, String counseleeId);
}
