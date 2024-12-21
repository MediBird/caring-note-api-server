package com.springboot.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.api.domain.Medication;

public interface MedicationRepository extends JpaRepository<Medication, String> {
    List<Medication> findByItemNameContainingIgnoreCase(String keyword);
    // 또는 더 세밀한 제어를 위해
    @Query(value = """
    SELECT m FROM Medication m 
    WHERE LOWER(m.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) 
    OR FUNCTION('REGEXP_LIKE', m.itemName, :pattern)
    """)
    List<Medication> searchByItemNameWithPattern(
        @Param("keyword") String keyword,
        @Param("pattern") String pattern
    );

}
