package com.springboot.api.service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselor.AddCounselorReq;
import com.springboot.api.dto.counselor.AddCounselorRes;
import com.springboot.api.repository.CounselorRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CounselorService {

    private final PasswordEncoder passwordEncoder;
    private final CounselorRepository counselorRepository;

    public CounselorService(PasswordEncoder passwordEncoder, CounselorRepository counselorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.counselorRepository = counselorRepository;
    }

    @Transactional
    public AddCounselorRes addCounselor(AddCounselorReq addCounselorReq) {

        // 비밀번호를 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(addCounselorReq.getPassword());

        Counselor counselor = Counselor.builder()
                .name(addCounselorReq.getName())
                .email(addCounselorReq.getEmail())
                .password(encodedPassword)
                .phoneNumber(addCounselorReq.getPhoneNumber())
                .roleType(addCounselorReq.getRoleType())
                .build();


        if (counselorRepository.existsByEmail(addCounselorReq.getEmail())) {
            throw new DuplicatedEmailException();
        }

        Counselor savedCounselor = counselorRepository.save(counselor);

        return new AddCounselorRes(savedCounselor.getId(), savedCounselor.getRoleType());

    }

}