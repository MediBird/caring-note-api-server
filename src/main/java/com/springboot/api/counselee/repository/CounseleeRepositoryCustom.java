package com.springboot.api.counselee.repository;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import com.springboot.api.counselee.entity.Counselee;

public interface CounseleeRepositoryCustom {

    PageRes<Counselee> findWithFilters(
        String name,
        List<LocalDate> birthDates,
        List<String> affiliatedWelfareInstitutions,
        PageReq pageReq);

    List<LocalDate> findDistinctBirthDates();

    List<String> findDistinctAffiliatedWelfareInstitutions();

    Optional<Counselee> findByCounselSessionId(String counselSessionId);

    List<Counselee> findByNameContaining(String keyword);
}