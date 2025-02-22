package com.springboot.api.repository;

import com.springboot.api.domain.Counselee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CounseleeRepositoryCustom {
    Page<Counselee> findWithFilters(
            String name,
            List<LocalDate> birthDates,
            List<String> affiliatedWelfareInstitutions,
            Pageable pageable);

    List<LocalDate> findDistinctBirthDates();

    List<String> findDistinctAffiliatedWelfareInstitutions();
}