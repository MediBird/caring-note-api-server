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
        assertNotNull(results, "검색 결과가 null이 아니어야 합니다");
        assertTrue(!results.isEmpty(), "검색 결과가 있어야 합니다");
        System.out.println("results: " + results);

        Medication firstResult = results.get(0);
        assertNotNull(firstResult, "첫 번째 결과가 null이 아니어야 합니다");
        assertTrue(firstResult.getItemNameChosung().contains(pattern), 
            "검색 결과의 초성은 패턴을 포함해야 합니다");
    }
    
    @Test
    void testSearchByItemNameWithKeyword() {
        String keyword = "제니로우";
        String pattern = "ㅈㄴㄹㅇ";

        // 메서드 호출
        List<Medication> results = medicationRepository.searchByItemNameWithPattern(keyword, pattern);

        // 검증
        assertNotNull(results, "검색 결과가 null이 아니어야 합니다");
        assertTrue(!results.isEmpty(), "검색 결과가 있어야 합니다");

        Medication firstResult = results.get(0);
        assertNotNull(firstResult, "첫 번째 결과가 null이 아니어야 합니다");
        assertTrue(firstResult.getItemName().contains(keyword), 
            "검색 결과는 키워드를 포함해야 합니다");
    }
}
