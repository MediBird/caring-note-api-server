package com.springboot.api.counselsession.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselsession.dto.counseleeconsent.AddCounseleeConsentReq;
import com.springboot.api.counselsession.dto.counseleeconsent.AddCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.DeleteCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.SelectCounseleeConsentByCounseleeIdRes;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentReq;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.CounseleeConsent;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.CounseleeConsentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CounseleeConsentService {

    private final CounseleeConsentRepository counseleeConsentRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final CounseleeRepository counseleeRepository;

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

    @Transactional
    public AddCounseleeConsentRes addCounseleeConsent(AddCounseleeConsentReq addCounseleeConsentReq) {

        CounselSession counselSession = counselSessionRepository
            .findById(addCounseleeConsentReq.getCounselSessionId())
            .orElseThrow(IllegalArgumentException::new);

        Counselee counselee = counseleeRepository.findById(addCounseleeConsentReq.getCounseleeId())
            .orElseThrow(IllegalArgumentException::new);

        CounseleeConsent counseleeConsent = CounseleeConsent.create(counselSession, counselee);
        CounseleeConsent savedCounselConsent = counseleeConsentRepository.save(counseleeConsent);

        return new AddCounseleeConsentRes(savedCounselConsent.getId());
    }

    @Transactional
    public UpdateCounseleeConsentRes updateCounseleeConsent(UpdateCounseleeConsentReq updateCounseleeConsentReq) {
        CounseleeConsent counseleeConsent = counseleeConsentRepository
            .findById(updateCounseleeConsentReq.getCounseleeConsentId())
            .orElseThrow(IllegalArgumentException::new);

        counseleeConsent.accept();

        return new UpdateCounseleeConsentRes(counseleeConsent.getId());
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
    public UpdateCounseleeConsentRes acceptCounseleeConsent(String counseleeConsentId) {
        CounseleeConsent counseleeConsent = counseleeConsentRepository
            .findById(counseleeConsentId)
            .orElseThrow(IllegalArgumentException::new);

        counseleeConsent.accept();

        return new UpdateCounseleeConsentRes(counseleeConsent.getId());
    }

    @Transactional
    public DeleteCounseleeConsentRes deleteCounseleeConsent(String counseleeConsentId) {

        CounseleeConsent counseleeConsent = counseleeConsentRepository.findById(counseleeConsentId)
            .orElseThrow(IllegalArgumentException::new);

        counseleeConsentRepository.deleteById(counseleeConsentId);

        return new DeleteCounseleeConsentRes(counseleeConsent.getId());
    }

}
