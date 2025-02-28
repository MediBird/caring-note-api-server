package com.springboot.api.counselor.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.counselor.dto.AddCounselorReq;
import com.springboot.api.counselor.dto.AddCounselorRes;
import com.springboot.api.counselor.dto.CounselorNameListRes;
import com.springboot.api.counselor.dto.GetCounselorRes;
import com.springboot.api.counselor.dto.SelectCounselorRes;
import com.springboot.api.counselor.dto.UpdateCounselorReq;
import com.springboot.api.counselor.dto.UpdateCounselorRes;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.repository.CounselorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselorService {

    private final CounselorRepository counselorRepository;

    @CacheEvict(value = "counselorNames", allEntries = true)
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

    /**
     * 모든 Counselor의 이름 목록을 반환합니다.
     * "counselorNames" 캐시에 결과가 저장됩니다.
     * 빈 문자열이나 null은 필터링됩니다.
     * 
     * @return CounselorNameListRes 객체 (이름 목록 포함)
     */
    @Cacheable("counselorNames")
    @Transactional
    public CounselorNameListRes getCounselorNames() {
        List<String> counselorNames = counselorRepository.findAll().stream()
                .map(Counselor::getName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .sorted()
                .collect(Collectors.toList());

        return new CounselorNameListRes(counselorNames);
    }

    /**
     * Counselor 정보를 업데이트하고 캐시를 무효화합니다.
     * 
     * @param counselorId        업데이트할 상담사 ID
     * @param updateCounselorReq 업데이트할 상담사 정보
     * @return 업데이트된 상담사 정보
     */
    @CacheEvict(value = "counselorNames", allEntries = true)
    @Transactional
    public UpdateCounselorRes updateCounselor(String counselorId, UpdateCounselorReq updateCounselorReq) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counselor ID: " + counselorId));

        // 이름이 변경된 경우에만 업데이트
        if (updateCounselorReq.getName() != null && !updateCounselorReq.getName().trim().isEmpty()) {
            counselor.setName(updateCounselorReq.getName());
        }

        if (updateCounselorReq.getPhoneNumber() != null) {
            counselor.setPhoneNumber(updateCounselorReq.getPhoneNumber());
        }

        if (updateCounselorReq.getRoleType() != null) {
            counselor.setRoleType(updateCounselorReq.getRoleType());
        }

        Counselor updatedCounselor = counselorRepository.save(counselor);

        return new UpdateCounselorRes(
                updatedCounselor.getId(),
                updatedCounselor.getName(),
                updatedCounselor.getRoleType());
    }

    /**
     * Counselor를 삭제하고 캐시를 무효화합니다.
     * 
     * @param counselorId 삭제할 상담사 ID
     */
    @CacheEvict(value = "counselorNames", allEntries = true)
    @Transactional
    public void deleteCounselor(String counselorId) {
        if (!counselorRepository.existsById(counselorId)) {
            throw new IllegalArgumentException("Invalid counselor ID: " + counselorId);
        }
        counselorRepository.deleteById(counselorId);
    }
}
