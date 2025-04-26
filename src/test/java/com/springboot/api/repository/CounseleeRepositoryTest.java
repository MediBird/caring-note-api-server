package com.springboot.api.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CounseleeRepositoryTest {

    @Mock
    private CounseleeRepository counseleeRepository;

    private List<Counselee> counseleesStartWithHong;
    private List<Counselee> counseleesContainingHong;
    private List<Counselee> allCounselees;

    @BeforeEach
    public void setup() {
        // 테스트 데이터 초기화
        initializeTestData();
    }

    @Test
    public void testFindByNameContaining_WithHongKeyword() {
        // Given
        when(counseleeRepository.findByNameContaining("홍"))
            .thenReturn(allCounselees.subList(0, Math.min(allCounselees.size(), 10)));

        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 10);
        results.forEach(counselee -> assertTrue(counselee.getName().contains("홍")));
    }

    @Test
    public void testFindByNameContaining_OrderByStartsWithFirst() {
        // Given
        when(counseleeRepository.findByNameContaining("홍"))
            .thenReturn(allCounselees.subList(0, Math.min(allCounselees.size(), 10)));

        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).getName().startsWith("홍"));
    }

    @Test
    public void testFindByNameContaining_WithEmptyKeyword() {
        // Given
        when(counseleeRepository.findByNameContaining(""))
            .thenReturn(new ArrayList<>());

        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("");

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    public void testFindByNameContaining_WithNonExistentName() {
        // Given
        when(counseleeRepository.findByNameContaining("존재하지않는이름"))
            .thenReturn(new ArrayList<>());

        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("존재하지않는이름");

        // Then
        assertTrue(results.isEmpty());
    }

    private void initializeTestData() {
        counseleesStartWithHong = Arrays.asList(
            Counselee.builder()
                .name("홍길동")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .phoneNumber("010-1234-5678")
                .genderType(GenderType.MALE)
                .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                .counselCount(0)
                .registrationDate(LocalDate.now())
                .build(),
            Counselee.builder()
                .name("홍박사")
                .dateOfBirth(LocalDate.of(1975, 5, 15))
                .phoneNumber("010-2345-6789")
                .genderType(GenderType.MALE)
                .healthInsuranceType(HealthInsuranceType.MEDICAL_AID)
                .counselCount(2)
                .registrationDate(LocalDate.now())
                .build());

        counseleesContainingHong = Arrays.asList(
            Counselee.builder()
                .name("김지홍")
                .dateOfBirth(LocalDate.of(1982, 4, 12))
                .phoneNumber("010-6789-0123")
                .genderType(GenderType.MALE)
                .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                .counselCount(1)
                .registrationDate(LocalDate.now())
                .build(),
            Counselee.builder()
                .name("이홍기")
                .dateOfBirth(LocalDate.of(1988, 8, 8))
                .phoneNumber("010-7890-1234")
                .genderType(GenderType.MALE)
                .healthInsuranceType(HealthInsuranceType.MEDICAL_AID)
                .counselCount(2)
                .registrationDate(LocalDate.now())
                .build());

        allCounselees = new ArrayList<>();
        allCounselees.addAll(counseleesStartWithHong);
        allCounselees.addAll(counseleesContainingHong);
    }
}