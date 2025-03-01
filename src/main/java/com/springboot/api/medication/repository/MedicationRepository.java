package com.springboot.api.medication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.api.counselsession.entity.Medication;

public interface MedicationRepository extends JpaRepository<Medication, String> {
        List<Medication> findByItemNameContainingIgnoreCase(String keyword);

        // 또는 더 세밀한 제어를 위해
        @Query(value = """
                            SELECT * FROM medications m
                            WHERE LOWER(m.item_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                            OR m.item_name_chosung ~ LOWER(CONCAT('.*', :pattern, '.*'))
                                ORDER BY
                                CASE
                                    WHEN LOWER(m.item_name_chosung) LIKE LOWER(CONCAT(:pattern, '%')) THEN 1
                                    WHEN LOWER(m.item_name_chosung) LIKE LOWER(CONCAT('%', :pattern, '%')) THEN 2
                                            ELSE 3
                                END
                        """, nativeQuery = true)
        List<Medication> searchByItemNameWithPattern(
                        @Param("keyword") String keyword,
                        @Param("pattern") String pattern);
}
