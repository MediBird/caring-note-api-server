package com.springboot.api.counselsession.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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

                CounseleeConsent counseleeConsent = CounseleeConsent
                                .builder()
                                .counselSession(counselSession)
                                .counselee(counselee)
                                .isConsent(addCounseleeConsentReq.isConsent())
                                .consentDateTime(LocalDateTime.now())
                                .build();
                CounseleeConsent savedCounselConsent = counseleeConsentRepository.save(counseleeConsent);

                return new AddCounseleeConsentRes(savedCounselConsent.getId());
        }

        @Transactional
        public UpdateCounseleeConsentRes updateCounseleeConsent(UpdateCounseleeConsentReq updateCounseleeConsentReq) {
                CounseleeConsent counseleeConsent = counseleeConsentRepository
                                .findById(updateCounseleeConsentReq.getCounseleeConsentId())
                                .orElseThrow(IllegalArgumentException::new);

                counseleeConsent.setConsentDateTime(LocalDateTime.now());
                counseleeConsent.setConsent(updateCounseleeConsentReq.isConsent());

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
