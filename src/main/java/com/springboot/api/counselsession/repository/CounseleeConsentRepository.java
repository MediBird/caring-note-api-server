package com.springboot.api.counselsession.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.CounseleeConsent;

public interface CounseleeConsentRepository extends JpaRepository<CounseleeConsent, String> {

    Optional<CounseleeConsent> findByCounselSessionId(String counselSessionId);

    Optional<CounseleeConsent> findByCounselSessionIdAndCounseleeId(String counselSessionId, String CounseleeId);

}
