package com.springboot.api.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.Medication;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
@Sql(scripts = "/sql/test-medication.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MedicationRepositoryTest {

    @Autowired
    private MedicationRepository medicationRepository;

    @Test
    void testSearchByItemNameWithPattern() {

        String keyword = "ㅈㄴㄹㅇ";
        String pattern = "ㅈㄴㄹㅇ";

        // 메서드 호출
        List<Medication> results = medicationRepository.searchByItemNameWithPattern(keyword, pattern);

        // 검증
        assertNotNull(results);
        assertTrue(results.stream()
                .allMatch(med -> med.getItemName().toLowerCase().contains(keyword.toLowerCase())
                        || med.getItemName().matches(pattern.replace("%", ".*"))));
    }
    
    @Test
    void testSearchByItemNameWithKeyword() {

        String keyword = "제니로우캡슐120밀리그램(오르리스타트)";
        String pattern = "ㅈㄴㄹㅇㅋㅅ120ㅁㄹㄱㄹ(ㅇㄹㄹㅅㅌㅌ)";

        // 메서드 호출
        List<Medication> results = medicationRepository.searchByItemNameWithPattern(keyword, pattern);

        // 검증
        assertNotNull(results);
        assertTrue(results.stream()
                .allMatch(med -> med.getItemName().toLowerCase().contains(keyword.toLowerCase()) 
                        || med.getItemName().matches(pattern.replace("%", ".*"))));
    }
}
