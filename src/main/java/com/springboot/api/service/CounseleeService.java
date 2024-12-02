package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselee.SelectByCounselSessionIdRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounseleeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CounseleeService {

    public final CounseleeRepository counseleeRepository;
    public final CounselSessionRepository counselSessionRepository;
    public final DateTimeUtil dateTimeUtil;

    public CounseleeService(CounseleeRepository counseleeRepository
    , CounselSessionRepository counselSessionRepository
    ,DateTimeUtil dateTimeUtil) {
        this.counseleeRepository = counseleeRepository;
        this.counselSessionRepository = counselSessionRepository;
        this.dateTimeUtil = dateTimeUtil;
    }

    public SelectByCounselSessionIdRes selectByCounselSessionId(String id, String counselSessionId,
                                                                String counseleeId) throws RuntimeException {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        Counselee counseleeInSession = Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);

        Counselee counselee = counseleeRepository.findById(counseleeId)
                .orElseThrow(NoContentException::new);

        Optional.of(counselee)
                .filter(c -> c.getId().equals(counseleeInSession.getId()))
                .orElseThrow(NoContentException::new);



        return new SelectByCounselSessionIdRes(
                counselee.getId()
                ,counselee.getName()
                ,dateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now())
                ,counselee.getDateOfBirth().toString()
                ,counselee.getGenderType()
                ,counselee.getAddress()
                ,counselee.getHealthInsuranceType()
                ,counselee.getCounselingCount()
                ,counselee.getLastCounselingDate()
        );

    }


}
