package com.springboot.api.counselor.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
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
import com.springboot.api.common.exception.ResourceNotFoundException;
import com.springboot.api.counselor.dto.AddCounselorReq;
import com.springboot.api.counselor.dto.AddCounselorRes;
import com.springboot.api.counselor.dto.CounselorNameListRes;
import com.springboot.api.counselor.dto.CounselorPageRes;
import com.springboot.api.counselor.dto.GetCounselorRes;
import com.springboot.api.counselor.dto.ResetPasswordReq;
import com.springboot.api.counselor.dto.SelectCounselorRes;
import com.springboot.api.counselor.dto.UpdateCounselorReq;
import com.springboot.api.counselor.dto.UpdateCounselorRes;
import com.springboot.api.counselor.dto.UpdateRoleReq;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.repository.CounselorRepository;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.enums.RoleType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounselorService {

    private final CounselorRepository counselorRepository;
    private final KeycloakUserService keycloakUserService;
    private final CounselSessionRepository counselSessionRepository;

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
        List<String> counselorNames = counselorRepository
                .findByRoleTypeIn(Arrays.asList(RoleType.ROLE_USER, RoleType.ROLE_ADMIN)).stream()
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
            String phoneNumber = updateCounselorReq.getPhoneNumber();
            counselor.setPhoneNumber(phoneNumber);
        }

        if (updateCounselorReq.getRoleType() != null) {
            counselor.setRoleType(updateCounselorReq.getRoleType());
        }

        if (updateCounselorReq.getDescription() != null) {
            counselor.setDescription(updateCounselorReq.getDescription());
        }

        Counselor updatedCounselor = counselorRepository.save(counselor);

        return new UpdateCounselorRes(
                updatedCounselor.getId(),
                updatedCounselor.getName(),
                updatedCounselor.getRoleType());
    }

    /**
     * Counselor를 삭제하고 캐시를 무효화합니다.
     * Keycloak에서도 해당 사용자를 삭제합니다.
     * 
     * @param counselorId 삭제할 상담사 ID
     */
    @CacheEvict(value = "counselorNames", allEntries = true)
    @Transactional
    public void deleteCounselor(String counselorId) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("상담사를 찾을 수 없습니다. ID: " + counselorId));

        try {
            // Keycloak에서 사용자 찾기 시도
            if (counselor.getUsername() != null) {
                List<UserRepresentation> users = keycloakUserService.getUsersByUsername(counselor.getUsername());
                if (!users.isEmpty()) {
                    // Keycloak에서 사용자 삭제
                    keycloakUserService.deleteUser(users.get(0).getId());
                    log.info("Keycloak에서 사용자 삭제 완료: {}", counselor.getUsername());
                }
            }
        } catch (Exception e) {
            log.error("Keycloak에서 사용자 삭제 중 오류 발생: {}", e.getMessage(), e);
            // Keycloak 삭제 실패해도 DB에서는 삭제 진행
        }

        // 상담사와 연결된 모든 상담 세션에서 상담사 참조를 null로 변경
        List<CounselSession> counselSessions = counselSessionRepository.findByCounselorId(counselor.getId());
        for (CounselSession session : counselSessions) {
            session.updateCounselor(null);
        }
        log.info("상담사({})와 연결된 상담 세션 {}개의 상담사 참조를 null로 변경했습니다.", counselor.getId(), counselSessions.size());

        // DB에서 상담사 삭제
        counselorRepository.deleteById(counselorId);
        log.info("DB에서 상담사 삭제 완료: {}", counselorId);
    }

    /**
     * 상담사의 비밀번호를 초기화합니다.
     * Keycloak에서 해당 사용자의 비밀번호를 초기화합니다.
     * 
     * @param counselorId      비밀번호를 초기화할 상담사 ID
     * @param resetPasswordReq 비밀번호 초기화 요청 정보
     */
    @Transactional
    public void resetPassword(String counselorId, ResetPasswordReq resetPasswordReq) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("상담사를 찾을 수 없습니다. ID: " + counselorId));

        if (counselor.getUsername() == null) {
            throw new IllegalStateException("사용자명이 없는 상담사입니다: " + counselorId);
        }

        try {
            // Keycloak에서 사용자 찾기
            List<UserRepresentation> users = keycloakUserService.getUsersByUsername(counselor.getUsername());
            if (users.isEmpty()) {
                throw new ResourceNotFoundException("Keycloak에서 사용자를 찾을 수 없습니다: " + counselor.getUsername());
            }

            // 비밀번호 초기화
            keycloakUserService.resetPassword(
                    users.get(0).getId(),
                    resetPasswordReq.getNewPassword(),
                    resetPasswordReq.isTemporary());

            log.info("사용자 비밀번호 초기화 완료: {}", counselor.getUsername());
        } catch (ResourceNotFoundException e) {
            log.error("비밀번호 초기화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("비밀번호 초기화 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 상담사의 권한을 변경합니다.
     * DB와 Keycloak 모두에서 권한을 업데이트합니다.
     * 
     * @param counselorId   권한을 변경할 상담사 ID
     * @param updateRoleReq 권한 변경 요청 정보
     * @return 업데이트된 상담사 정보
     */
    @Transactional
    public UpdateCounselorRes updateRole(String counselorId, UpdateRoleReq updateRoleReq) {
        Counselor counselor = counselorRepository.findById(counselorId)
                .orElseThrow(() -> new ResourceNotFoundException("상담사를 찾을 수 없습니다. ID: " + counselorId));

        if (counselor.getUsername() == null) {
            throw new IllegalStateException("사용자명이 없는 상담사입니다: " + counselorId);
        }

        // DB에서 권한 업데이트
        counselor.setRoleType(updateRoleReq.getRoleType());
        Counselor updatedCounselor = counselorRepository.save(counselor);
        log.info("DB에서 상담사 권한 업데이트 완료: {} -> {}", counselorId, updateRoleReq.getRoleType());

        return new UpdateCounselorRes(
                updatedCounselor.getId(),
                updatedCounselor.getName(),
                updatedCounselor.getRoleType());
    }

    @Transactional
    public CounselorPageRes getCounselorsByPage(int page, int size) {
        // RoleType.ROLE_ASSISTANT가 맨 위로 오도록 정렬하는 커스텀 쿼리 메서드를 사용
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Counselor> counselorPage = counselorRepository.findAllWithRoleTypeOrder(pageRequest);

        return CounselorPageRes.fromPage(counselorPage);
    }
}
