package com.springboot.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselor.AddCounselorReq;
import com.springboot.api.dto.counselor.AddCounselorRes;
import com.springboot.api.dto.counselor.GetCounselorRes;
import com.springboot.api.dto.counselor.SelectCounselorRes;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.api.repository.CounselSessionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String username = jwt.getClaimAsString("preferred_username");

        Counselor counselor = counselorRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counselor ID"));

        return new GetCounselorRes(counselor.getId(), counselor.getName(), counselor.getEmail(),
                counselor.getPhoneNumber(), counselor.getRoleType(), counselor.getMedicationCounselingCount(),
                counselor.getCounseledCounseleeCount(), counselor.getParticipationDays());
    }

    @Transactional
    public List<SelectCounselorRes> selectCounselors(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Counselor> counselorPage = counselorRepository.findAll(pageRequest);

        return counselorPage.getContent().stream()
                .sorted(Comparator.comparing(Counselor::getCreatedDatetime).reversed())
                .map(counselor -> SelectCounselorRes.builder()
                        .id(counselor.getId())
                        .name(counselor.getName())
                        .email(counselor.getEmail())
                        .phoneNumber(counselor.getPhoneNumber())
                        .roleType(counselor.getRoleType())
                        .medicationCounselingCount(counselor.getMedicationCounselingCount())
                        .counseledCounseleeCount(counselor.getCounseledCounseleeCount())
                        .participationDays(counselor.getParticipationDays())
                        .build())
                .collect(Collectors.toList());
    }

}
