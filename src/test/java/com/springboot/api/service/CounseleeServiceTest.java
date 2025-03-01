package com.springboot.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.springboot.api.config.QuerydslConfig;
import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselee.service.CounseleeService;

@ExtendWith(MockitoExtension.class)
@Import(QuerydslConfig.class)
public class CounseleeServiceTest {

    @Mock
    private CounseleeRepository counseleeRepository;

    @InjectMocks
    private CounseleeService counseleeService;

    private List<Counselee> hongCounselees;

    @BeforeEach
    public void setup() {
        // "홍"을 포함하는 내담자 10명 설정
        hongCounselees = createCounseleeList();
    }

    @Test
    public void testSearchCounseleesByName_WithHongKeyword() {
        // Given
        when(counseleeRepository.findByNameContaining("홍")).thenReturn(hongCounselees);

        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("홍");

        // Then
        assertEquals(10, result.size());
        assertEquals("홍박사", result.get(0).getName());
        assertEquals("다홍치마", result.get(9).getName());
    }

    @Test
    public void testSearchCounseleesByName_WithEmptyKeyword() {
        // Given
        // When
        List<SelectCounseleeAutocompleteRes> result = counseleeService.searchCounseleesByName("");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchCounseleesByName_WithNullKeyword() {
        // Given
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
        List<Counselee> mixedCounselees = Arrays.asList(
                createMockCounselee("id1", "백홍준", LocalDate.of(1982, 2, 2)),
                createMockCounselee("id2", "홍박사", LocalDate.of(1980, 1, 1)),
                createMockCounselee("id3", "김지홍", LocalDate.of(1985, 3, 3)),
                createMockCounselee("id4", "홍세달", LocalDate.of(1987, 4, 4)));

        // 레포지토리에서 정렬된 결과를 반환하도록 설정
        List<Counselee> sortedCounselees = Arrays.asList(
                createMockCounselee("id2", "홍박사", LocalDate.of(1980, 1, 1)),
                createMockCounselee("id4", "홍세달", LocalDate.of(1987, 4, 4)),
                createMockCounselee("id1", "백홍준", LocalDate.of(1982, 2, 2)),
                createMockCounselee("id3", "김지홍", LocalDate.of(1985, 3, 3)));

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

    private List<Counselee> createCounseleeList() {
        // 테스트용 내담자 목록 생성 로직
        // 실제 구현에서는 Counselee 객체를 생성하는 코드가 필요합니다
        // 여기서는 간단한 예시로 비어있는 리스트를 반환합니다
        return Arrays.asList(
                createMockCounselee("id1", "홍박사", LocalDate.of(1980, 1, 1)),
                createMockCounselee("id2", "백홍준", LocalDate.of(1982, 2, 2)),
                createMockCounselee("id3", "김지홍", LocalDate.of(1985, 3, 3)),
                createMockCounselee("id4", "홍세달", LocalDate.of(1987, 4, 4)),
                createMockCounselee("id5", "임달홍", LocalDate.of(1989, 5, 5)),
                createMockCounselee("id6", "황비홍", LocalDate.of(1990, 6, 6)),
                createMockCounselee("id7", "홍명보", LocalDate.of(1991, 7, 7)),
                createMockCounselee("id8", "홍다는", LocalDate.of(1992, 8, 8)),
                createMockCounselee("id9", "홍체념", LocalDate.of(1993, 9, 9)),
                createMockCounselee("id10", "다홍치마", LocalDate.of(1995, 10, 10)));
    }

    private Counselee createMockCounselee(String id, String name, LocalDate dateOfBirth) {
        Counselee counselee = mock(Counselee.class);

        // 모킹된 객체의 메서드 동작 설정
        when(counselee.getId()).thenReturn(id);
        when(counselee.getName()).thenReturn(name);
        when(counselee.getDateOfBirth()).thenReturn(dateOfBirth);

        return counselee;
    }
}