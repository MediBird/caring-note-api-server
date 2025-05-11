package com.springboot.api.counselsession.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselsession.dto.counseleeconsent.DeleteCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.SelectCounseleeConsentByCounseleeIdRes;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.CounseleeConsent;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.CounseleeConsentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounseleeConsentService {

    private final CounseleeConsentRepository counseleeConsentRepository;
    private final CounselSessionRepository counselSessionRepository;

    public SelectCounseleeConsentByCounseleeIdRes selectCounseleeConsentByCounseleeId(String counselSessionId,
        String counseleeId) {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
            .orElseThrow(IllegalArgumentException::new);

        if (!counseleeId.equals(counselee.getId())) {
            throw new IllegalArgumentException();
        }

        CounseleeConsent counseleeConsent = counseleeConsentRepository
            .findByCounselSessionIdAndCounseleeId(counselSessionId, counseleeId)
            .orElseThrow(NoContentException::new);

        return new SelectCounseleeConsentByCounseleeIdRes(counseleeConsent.getId(),
            counselee.getId(),
            counselee.getName(),
            counselSession.getId(),
            counseleeConsent.getConsentDateTime(),
            counseleeConsent.isConsent());

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void initializeCounseleeConsent(CounselSession counselSession, Counselee counselee) {
        if (counseleeConsentRepository.existsByCounselSessionIdAndCounseleeId(counselSession.getId(),
            counselee.getId())) {
            throw new IllegalArgumentException("해당 상담 세션에 대한 내담자 동의가 이미 존재합니다");
        }

        CounseleeConsent counseleeConsent = CounseleeConsent.create(counselSession, counselee);
        counseleeConsentRepository.save(counseleeConsent);
    }

    @Transactional
    public UpdateCounseleeConsentRes acceptCounseleeConsent(String counselSessionId) {
        CounseleeConsent counseleeConsent = counseleeConsentRepository
            .findByCounselSessionId(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counseleeConsent.accept();

        return new UpdateCounseleeConsentRes(counseleeConsent.getId());
    }

    @Transactional
    public DeleteCounseleeConsentRes deleteCounseleeConsent(String counselSessionId) {

        CounseleeConsent counseleeConsent = counseleeConsentRepository.findByCounselSessionId(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        counseleeConsentRepository.deleteById(counseleeConsent.getId());

        return new DeleteCounseleeConsentRes(counseleeConsent.getId());
    }

}
