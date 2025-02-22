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

        @Query(value = """
                            SELECT * FROM counselees c
                            WHERE (:name IS NULL OR c.name ILIKE CONCAT('%', :name, '%'))
                            AND (CAST(:birthDates AS date[]) IS NULL OR ARRAY_LENGTH(CAST(:birthDates AS date[]), 1) IS NULL
                                OR c.date_of_birth = ANY(CAST(:birthDates AS date[])))
                            AND (CAST(:affiliatedWelfareInstitutions AS varchar[]) IS NULL
                                OR ARRAY_LENGTH(CAST(:affiliatedWelfareInstitutions AS varchar[]), 1) IS NULL
                                OR c.affiliated_welfare_institution = ANY(CAST(:affiliatedWelfareInstitutions AS varchar[])))
                            ORDER BY c.registration_date DESC
                        """, countQuery = """
                            SELECT COUNT(*) FROM counselees c
                            WHERE (:name IS NULL OR c.name ILIKE CONCAT('%', :name, '%'))
                            AND (CAST(:birthDates AS date[]) IS NULL OR ARRAY_LENGTH(CAST(:birthDates AS date[]), 1) IS NULL
                                OR c.date_of_birth = ANY(CAST(:birthDates AS date[])))
                            AND (CAST(:affiliatedWelfareInstitutions AS varchar[]) IS NULL
                                OR ARRAY_LENGTH(CAST(:affiliatedWelfareInstitutions AS varchar[]), 1) IS NULL
                                OR c.affiliated_welfare_institution = ANY(CAST(:affiliatedWelfareInstitutions AS varchar[])))
                        """, nativeQuery = true)
        Page<Counselee> findWithFilters(
                        @Param("name") String name,
                        @Param("birthDates") List<LocalDate> birthDates,
                        @Param("affiliatedWelfareInstitutions") List<String> affiliatedWelfareInstitutions,
                        Pageable pageable);

        @Query("SELECT DISTINCT c.dateOfBirth FROM Counselee c ORDER BY c.dateOfBirth DESC")
        List<LocalDate> findDistinctBirthDates();

        @Query("SELECT DISTINCT c.affiliatedWelfareInstitution FROM Counselee c " +
                        "WHERE c.affiliatedWelfareInstitution IS NOT NULL " +
                        "ORDER BY c.affiliatedWelfareInstitution")
        List<String> findDistinctAffiliatedWelfareInstitutions();

}
