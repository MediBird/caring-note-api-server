package com.springboot.api.counselor.repository;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.enums.RoleType;
import java.util.List;
import java.util.Optional;

import com.springboot.api.counselor.entity.Counselor;

public interface CounselorRepositoryCustom {

    Boolean existsActiveByEmail(String email);

    Optional<Counselor> findActiveByUsername(String username);

    Optional<Counselor> findActiveById(String counselorId);


    List<Counselor> findActiveByRoleTypes(List<RoleType> roleTypes);

    PageRes<Counselor> findAllActiveWithRoleTypeOrder(PageReq pageReq);
}