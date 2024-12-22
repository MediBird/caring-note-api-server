package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.CounseleeConsent;
import com.springboot.api.dto.counseleeconsent.*;
import com.springboot.api.repository.CounseleeConsentRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounseleeConsentService {

    private final CounseleeConsentRepository counseleeConsentRepository;
    private final EntityManager entityManager;


    public SelectCounseleeConsentByCounseleeIdRes selectCounseleeConsentByCounseleeId(String counselSessionId, String counseleeId)
    {

        CounseleeConsent counseleeConsent = counseleeConsentRepository.findByCounselSessionIdAndCounseleeId(counselSessionId,counseleeId)
                .orElseThrow(NoContentException::new);

        Counselee counselee = Optional.ofNullable(counseleeConsent.getCounselee()).orElseGet(Counselee::new);
        CounselSession counselSession = Optional.ofNullable(counseleeConsent.getCounselSession()).orElseGet(CounselSession::new);

       return new SelectCounseleeConsentByCounseleeIdRes(counseleeConsent.getId()
               ,counselee.getId()
               ,counselee.getName()
               ,counselSession.getId()
               ,counseleeConsent.getConsentDateTime()
               ,counseleeConsent.isConsent()
       );

    }

    @Transactional
    public AddCounseleeConsentRes addCounseleeConsent(AddCounseleeConsentReq addCounseleeConsentReq)
    {
        CounselSession proxyCounselSession = entityManager.getReference(CounselSession.class, addCounseleeConsentReq.getCounselSessionId());
        Counselee proxyCounselee = entityManager.getReference(Counselee.class, addCounseleeConsentReq.getCounseleeId());


        CounseleeConsent counseleeConsent = CounseleeConsent
                .builder()
                .counselSession(proxyCounselSession)
                .counselee(proxyCounselee)
                .isConsent(addCounseleeConsentReq.isConsent())
                .consentDateTime(LocalDateTime.now())
                .build();
       CounseleeConsent savedCounselConsent =  counseleeConsentRepository.save(counseleeConsent);


       return new AddCounseleeConsentRes(savedCounselConsent.getId());
    }


    @Transactional
    public UpdateCounseleeConsentRes updateCounseleeConsent(UpdateCounseleeConsentReq updateCounseleeConsentReq)
    {
        CounseleeConsent counseleeConsent = counseleeConsentRepository.findById(updateCounseleeConsentReq.getCounseleeConsentId())
                .orElseThrow(NoContentException::new);


        counseleeConsent.setConsentDateTime(LocalDateTime.now());
        counseleeConsent.setConsent(updateCounseleeConsentReq.isConsent());

        return new UpdateCounseleeConsentRes(counseleeConsent.getId());
    }

    @Transactional
    public DeleteCounseleeConsentRes deleteCounseleeConsent(DeleteCounseleeConsentReq deleteCounseleeConsentReq){

        CounseleeConsent counseleeConsent = counseleeConsentRepository.findById(deleteCounseleeConsentReq.getCounseleeConsentId())
                .orElseThrow(NoContentException::new);


        counseleeConsentRepository.deleteById(deleteCounseleeConsentReq.getCounseleeConsentId());


        return new DeleteCounseleeConsentRes(counseleeConsent.getId());
    }

}
