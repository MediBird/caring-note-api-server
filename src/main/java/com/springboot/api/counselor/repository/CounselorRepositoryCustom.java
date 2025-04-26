package com.springboot.api.counselor.repository;

import com.springboot.enums.RoleType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.api.counselor.entity.Counselor;

public interface CounselorRepositoryCustom {

    Boolean existsActiveByEmail(String email);

    Optional<Counselor> findActiveByUsername(String username);

    Optional<Counselor> findActiveById(String counselorId);


    List<Counselor> findActiveByRoleTypes(List<RoleType> roleTypes);

    /**
     * ROLE_ASSISTANT가 맨 위에 오도록 정렬된 상담사 목록을 페이징하여 조회
     *
     * @param pageable 페이징 정보
     * @return ROLE_ASSISTANT가 먼저 오고, 그 다음에 업데이트 시간 기준 내림차순으로 정렬된 상담사 목록
     */
    Page<Counselor> findAllActiveWithRoleTypeOrder(Pageable pageable);
}