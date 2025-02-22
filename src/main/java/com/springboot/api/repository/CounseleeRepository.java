package com.springboot.api.repository;

import com.springboot.api.domain.Counselee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CounseleeRepository extends JpaRepository<Counselee, String> {

        @Query("SELECT DISTINCT c.dateOfBirth FROM Counselee c ORDER BY c.dateOfBirth DESC")
        List<LocalDate> findDistinctBirthDates();

        @Query("SELECT DISTINCT c.affiliatedWelfareInstitution FROM Counselee c " +
                        "WHERE c.affiliatedWelfareInstitution IS NOT NULL " +
                        "ORDER BY c.affiliatedWelfareInstitution")
        List<String> findDistinctAffiliatedWelfareInstitutions();

}
