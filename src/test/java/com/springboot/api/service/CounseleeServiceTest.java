package com.springboot.api.service;

import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselee.service.CounseleeService;
import com.springboot.api.fixture.CounseleeFixture;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounseleeServiceTest {

    private CounseleeFixture counseleeFixture = new CounseleeFixture();

    @Mock
    private CounseleeRepository counseleeRepository;

    @Mock
    private CounselCardRepository counselCardRepository;

    @InjectMocks
    private CounseleeService counseleeService;

    @Test
    public void testSearchCounseleesByName_WithRandomKeyword() {
        // Given
        List<Counselee> counselees = counseleeFixture.createCounselees(10);

        String randomKeyword = Optional.of(counselees)
            .filter(list -> !list.isEmpty())
            .map(list -> list.get(new Random().nextInt(list.size())))
            .map(c -> c.getName().substring(1))
            .orElseThrow();

        List<Counselee> matched = counselees.stream()
            .filter(c -> c.getName().contains(randomKeyword))
            .toList();

        given(counseleeRepository.findByNameContaining(randomKeyword))
            .willReturn(matched);

        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName(randomKeyword);

        // Then
        assertEquals(matched.size(), result.size());
        for (int i = 0; i < matched.size(); i++) {
            assertEquals(matched.get(i).getName(), result.get(i).getName());
        }
    }


    @Test
    public void testSearchCounseleesByName_WithEmptyKeyword() {
        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchCounseleesByName_WithNullKeyword() {
        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchCounseleesByName_WithNonExistentName() {
        // Given
        when(counseleeRepository.findByNameContaining("존재하지않는이름")).thenReturn(Collections.emptyList());

        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("존재하지않는이름");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchCounseleesByName_OrderByStartsWithFirst() {
        // Given
        List<Counselee> sortedCounselees = Arrays.asList(
            counseleeFixture.createCounselee("홍박사"),
            counseleeFixture.createCounselee("홍세달"),
            counseleeFixture.createCounselee("백홍준"),
            counseleeFixture.createCounselee("김지홍"));

        when(counseleeRepository.findByNameContaining("홍")).thenReturn(sortedCounselees);

        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("홍");

        // Then
        assertEquals(4, result.size());
        // "홍"으로 시작하는 이름이 먼저 나오는지 확인
        assertEquals("홍박사", result.get(0).getName());
        assertEquals("홍세달", result.get(1).getName());
        // 그 다음 "홍"이 포함된 이름이 나오는지 확인
        assertTrue(result.get(2).getName().contains("홍"));
        assertTrue(result.get(3).getName().contains("홍"));
    }
}