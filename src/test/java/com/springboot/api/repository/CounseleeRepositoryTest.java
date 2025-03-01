package com.springboot.api.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.springboot.api.config.QuerydslConfig;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

@DataJpaTest
@Import(QuerydslConfig.class)
public class CounseleeRepositoryTest {

    @Autowired
    private CounseleeRepository counseleeRepository;

    @BeforeEach
    public void setup() {
        // 테스트 실행 전 기존 데이터 삭제
        counseleeRepository.deleteAll();
        // 테스트 데이터 생성 및 저장
        createAndSaveTestCounselees();
    }

    @Test
    public void testFindByNameContaining_WithHongKeyword() {
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        // 검색 결과가 있는지 확인
        assertTrue(results.size() > 0);

        // 모든 결과가 "홍"을 포함하는지 확인
        for (Counselee counselee : results) {
            assertTrue(counselee.getName().contains("홍"));
        }
    }

    @Test
    public void testFindByNameContaining_OrderByStartsWithFirst() {
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        // 결과가 있는지 확인
        assertTrue(results.size() > 0);

        boolean foundStartsWith = false;
        boolean foundContains = false;

        // "홍"으로 시작하는 이름이 먼저 나오는지 확인
        for (int i = 0; i < results.size(); i++) {
            String name = results.get(i).getName();

            if (name.startsWith("홍")) {
                foundStartsWith = true;
                // "홍"으로 시작하는 이름 다음에 "홍"이 포함된 이름이 나오면 안됨
                if (foundContains) {
                    assertTrue(false, "홍으로 시작하는 이름이 홍이 포함된 이름 뒤에 나옴");
                }
            } else if (name.contains("홍")) {
                foundContains = true;
            }
        }

        // 두 종류의 이름이 모두 있는지 확인
        assertTrue(foundStartsWith && foundContains, "홍으로 시작하는 이름과 홍이 포함된 이름이 모두 있어야 함");
    }

    @Test
    public void testFindByNameContaining_LimitTo10() {
        // 11개 이상의 결과가 있을 때 10개만 반환하는지 확인
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        assertTrue(results.size() <= 10, "검색 결과는 최대 10개로 제한되어야 함");
    }

    @Test
    public void testFindByNameContaining_WithEmptyKeyword() {
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("");

        // Then
        // 빈 키워드로 검색 시 결과가 없어야 함
        assertTrue(results.isEmpty(), "빈 키워드로 검색 시 결과가 없어야 함");
    }

    @Test
    public void testFindByNameContaining_WithNonExistentName() {
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("존재하지않는이름");

        // Then
        // 존재하지 않는 이름으로 검색 시 결과가 없어야 함
        assertTrue(results.isEmpty(), "존재하지 않는 이름으로 검색 시 결과가 없어야 함");
    }

    @Test
    public void testFindByNameContaining_CaseInsensitive() {
        // When - 소문자로 검색
        List<Counselee> resultsLower = counseleeRepository.findByNameContaining("홍");

        // Then
        assertFalse(resultsLower.isEmpty(), "검색 결과가 있어야 함");

        // 모든 결과가 "홍"을 포함하는지 확인
        for (Counselee counselee : resultsLower) {
            assertTrue(counselee.getName().toLowerCase().contains("홍"),
                    "모든 결과는 '홍'을 포함해야 함");
        }
    }

    @Test
    public void testFindByNameContaining_VerifyFirstResult() {
        // When
        List<Counselee> results = counseleeRepository.findByNameContaining("홍");

        // Then
        assertFalse(results.isEmpty(), "검색 결과가 있어야 함");

        // 첫 번째 결과는 "홍"으로 시작하는 이름이어야 함
        assertTrue(results.get(0).getName().startsWith("홍"),
                "첫 번째 결과는 '홍'으로 시작하는 이름이어야 함");
    }

    private void createAndSaveTestCounselees() {
        // 테스트 데이터 생성 - "홍"으로 시작하는 이름
        List<Counselee> counseleesStartWithHong = Arrays.asList(
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
                        .build(),
                Counselee.builder()
                        .name("홍세달")
                        .dateOfBirth(LocalDate.of(1990, 10, 20))
                        .phoneNumber("010-3456-7890")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(1)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("홍연희")
                        .dateOfBirth(LocalDate.of(1985, 3, 25))
                        .phoneNumber("010-4567-8901")
                        .genderType(GenderType.FEMALE)
                        .healthInsuranceType(HealthInsuranceType.VETERANS_BENEFITS)
                        .counselCount(3)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("홍시")
                        .dateOfBirth(LocalDate.of(1995, 7, 7))
                        .phoneNumber("010-5678-9012")
                        .genderType(GenderType.FEMALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build());

        // 테스트 데이터 생성 - 이름에 "홍"이 포함된 이름
        List<Counselee> counseleesContainingHong = Arrays.asList(
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
                        .build(),
                Counselee.builder()
                        .name("박홍준")
                        .dateOfBirth(LocalDate.of(1979, 12, 25))
                        .phoneNumber("010-8901-2345")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(4)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("최홍만")
                        .dateOfBirth(LocalDate.of(1970, 6, 30))
                        .phoneNumber("010-9012-3456")
                        .genderType(GenderType.MALE)
                        .healthInsuranceType(HealthInsuranceType.NON_COVERED)
                        .counselCount(0)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("정홍자")
                        .dateOfBirth(LocalDate.of(1965, 9, 15))
                        .phoneNumber("010-0123-4567")
                        .genderType(GenderType.FEMALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(5)
                        .registrationDate(LocalDate.now())
                        .build(),
                Counselee.builder()
                        .name("다홍치마")
                        .dateOfBirth(LocalDate.of(1992, 2, 14))
                        .phoneNumber("010-1111-2222")
                        .genderType(GenderType.FEMALE)
                        .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                        .counselCount(1)
                        .registrationDate(LocalDate.now())
                        .build());

        // 모든 테스트 데이터 저장
        counseleeRepository.saveAll(counseleesStartWithHong);
        counseleeRepository.saveAll(counseleesContainingHong);
    }
}