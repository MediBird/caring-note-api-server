package com.springboot.api.service;

import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselee.service.CounseleeService;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounseleeServiceTest {

    @Mock
    private CounseleeRepository counseleeRepository;

    @InjectMocks
    private CounseleeService counseleeService;

    @Test
    public void testSearchCounseleesByName_WithHongKeyword() {
        // Given
        List<Counselee> hongCounselees = Arrays.asList(
                Counselee.builder()
                        .name("홍박사")
                        .dateOfBirth(LocalDate.of(1980, 1, 1))
                        .phoneNumber("010-1234-5678")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("홍세달")
                        .dateOfBirth(LocalDate.of(1987, 4, 4))
                        .phoneNumber("010-2345-6789")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("백홍준")
                        .dateOfBirth(LocalDate.of(1982, 2, 2))
                        .phoneNumber("010-3456-7890")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build());
        when(counseleeRepository.findByNameContaining("홍")).thenReturn(hongCounselees);

        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("홍");

        // Then
        assertEquals(3, result.size());
        assertEquals("홍박사", result.get(0).getName());
        assertEquals("홍세달", result.get(1).getName());
        assertEquals("백홍준", result.get(2).getName());
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
                Counselee.builder()
                        .name("홍박사")
                        .dateOfBirth(LocalDate.of(1980, 1, 1))
                        .phoneNumber("010-1234-5678")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("홍세달")
                        .dateOfBirth(LocalDate.of(1987, 4, 4))
                        .phoneNumber("010-2345-6789")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("백홍준")
                        .dateOfBirth(LocalDate.of(1982, 2, 2))
                        .phoneNumber("010-3456-7890")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("김지홍")
                        .dateOfBirth(LocalDate.of(1985, 3, 3))
                        .phoneNumber("010-4567-8901")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build());

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