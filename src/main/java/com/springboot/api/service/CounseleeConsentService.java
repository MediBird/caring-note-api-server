package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.CounseleeConsent;
import com.springboot.api.dto.counseleeconsent.*;
import com.springboot.api.repository.CounseleeConsentRepository;
import com.springboot.api.repository.CounseleeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CounseleeConsentService {

        private final CounseleeConsentRepository counseleeConsentRepository;
        private final CounseleeRepository counseleeRepository;

        public SelectCounseleeConsentByCounseleeIdRes selectCounseleeConsentByCounseleeId(
                        String counseleeId) {

                Counselee counselee = counseleeRepository.findById(counseleeId)
                                .orElseThrow(IllegalArgumentException::new);

                if (!counseleeId.equals(counselee.getId())) {
                        throw new IllegalArgumentException();
                }

                CounseleeConsent counseleeConsent = counseleeConsentRepository
                                .findByCounseleeId(counseleeId)
                                .orElseThrow(NoContentException::new);

                return new SelectCounseleeConsentByCounseleeIdRes(counseleeConsent.getId(),
                                counselee.getId(),
                                counselee.getName(),
                                counseleeConsent.getConsentDateTime(),
                                counseleeConsent.isConsent());

        }

        @Transactional
        public AddCounseleeConsentRes addCounseleeConsent(AddCounseleeConsentReq addCounseleeConsentReq) {

                Counselee counselee = counseleeRepository.findById(addCounseleeConsentReq.getCounseleeId())
                                .orElseThrow(IllegalArgumentException::new);

                CounseleeConsent counseleeConsent = CounseleeConsent
                                .builder()
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
        public DeleteCounseleeConsentRes deleteCounseleeConsent(DeleteCounseleeConsentReq deleteCounseleeConsentReq) {

                CounseleeConsent counseleeConsent = counseleeConsentRepository
                                .findById(deleteCounseleeConsentReq.getCounseleeConsentId())
                                .orElseThrow(IllegalArgumentException::new);

                counseleeConsentRepository.deleteById(deleteCounseleeConsentReq.getCounseleeConsentId());

                return new DeleteCounseleeConsentRes(counseleeConsent.getId());
        }

}
