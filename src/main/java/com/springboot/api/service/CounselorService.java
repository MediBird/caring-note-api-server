package com.springboot.api.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselor.AddCounselorReq;
import com.springboot.api.dto.counselor.AddCounselorRes;
import com.springboot.api.dto.counselor.GetCounselorRes;
import com.springboot.api.repository.CounselorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselorService {

    private final CounselorRepository counselorRepository;

    @Transactional
    public AddCounselorRes addCounselor(AddCounselorReq addCounselorReq) {

        Counselor counselor = Counselor.builder()
                .name(addCounselorReq.getName())
                .email(addCounselorReq.getEmail())
                .password("encodedPassword")
                .phoneNumber(addCounselorReq.getPhoneNumber())
                .roleType(addCounselorReq.getRoleType())
                .build();

        if (counselorRepository.existsByEmail(addCounselorReq.getEmail())) {
            throw new DuplicatedEmailException();
        }

        Counselor savedCounselor = counselorRepository.save(counselor);

        return new AddCounselorRes(savedCounselor.getId(), savedCounselor.getRoleType());

    }

    @Transactional
    public GetCounselorRes getMyInfo() {
        Counselor counselor = counselorRepository
                .findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid counselor ID"));

        return new GetCounselorRes(counselor.getId(), counselor.getName(), counselor.getEmail(), counselor.getPhoneNumber(), counselor.getRoleType(), counselor.getMedicationCounselingCount(), counselor.getCounseledCounseleeCount(), counselor.getParticipationDays());
    }

}
