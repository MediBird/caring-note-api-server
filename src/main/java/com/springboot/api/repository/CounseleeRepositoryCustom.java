package com.springboot.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.springboot.api.domain.Counselee;

public interface CounseleeRepositoryCustom {
    Page<Counselee> findWithFilters(
            String name,
            List<LocalDate> birthDates,
            List<String> affiliatedWelfareInstitutions,
            Pageable pageable);

    List<LocalDate> findDistinctBirthDates();

    List<String> findDistinctAffiliatedWelfareInstitutions();

    Optional<Counselee> findByCounselSessionId(String counselSessionId);
}