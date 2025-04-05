package com.springboot.api.service;

import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.dto.UpdateCounseleeReq;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselee.service.CounseleeService;
import com.springboot.api.fixture.CounseleeFixture;
import com.springboot.enums.GenderType;
import java.time.LocalDate;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CounseleeServiceTest {

    @Mock
    private CounseleeRepository counseleeRepository;

    @InjectMocks
    private CounseleeService counseleeService;

    @Test
    public void updateCounselee_all(){
        //Given
        Counselee original = CounseleeFixture.create();

        UpdateCounseleeReq request = UpdateCounseleeReq.builder()
            .name("홍길동")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .phoneNumber("010-1234-5678")
            .lastCounselDate(LocalDate.of(2023, 10, 1))
            .affiliatedWelfareInstitution("서울복지관")
            .note("상담 노트")
            .genderType(GenderType.FEMALE)
            .address("서울시 강남구")
            .isDisability(true)
            .careManagerName("김철수")
            .build();
        //When

        original.update(request);
        //Then
        assertThat(original.getName()).isEqualTo(request.getName());
        assertThat(original.getDateOfBirth()).isEqualTo(request.getDateOfBirth());
        assertThat(original.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
        assertThat(original.getLastCounselDate()).isEqualTo(request.getLastCounselDate());
        assertThat(original.getAffiliatedWelfareInstitution()).isEqualTo(request.getAffiliatedWelfareInstitution());
        assertThat(original.getNote()).isEqualTo(request.getNote());
        assertThat(original.getGenderType()).isEqualTo(request.getGenderType());
        assertThat(original.getAddress()).isEqualTo(request.getAddress());
        assertThat(original.getIsDisability()).isEqualTo(request.getIsDisability());
        assertThat(original.getCareManagerName()).isEqualTo(request.getCareManagerName());
    }

    @Test
    public void updateCounselee_none(){
        //Given
        Counselee original = CounseleeFixture.create();

        String name = original.getName();
        String phoneNumber = original.getPhoneNumber();
        LocalDate dateOfBirth = original.getDateOfBirth();
        GenderType genderType = original.getGenderType();
        String address = original.getAddress();
        Boolean isDisability = original.getIsDisability();
        String note = original.getNote();
        String careManagerName = original.getCareManagerName();
        String affiliatedWelfareInstitution = original.getAffiliatedWelfareInstitution();

        UpdateCounseleeReq updateCounseleeReq = UpdateCounseleeReq.builder().build();
        //When
        original.update(updateCounseleeReq);
        //Then
        assertThat(original.getName()).isEqualTo(name);
        assertThat(original.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(original.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(original.getGenderType()).isEqualTo(genderType);
        assertThat(original.getAddress()).isEqualTo(address);
        assertThat(original.getIsDisability()).isEqualTo(isDisability);
        assertThat(original.getNote()).isEqualTo(note);
        assertThat(original.getCareManagerName()).isEqualTo(careManagerName);
        assertThat(original.getAffiliatedWelfareInstitution()).isEqualTo(affiliatedWelfareInstitution);
    }

    @Test
    public void testSearchCounseleesByName_WithRandomKeyword() {
        // Given
        List<Counselee> counselees = CounseleeFixture.createList(10);

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
            CounseleeFixture.create("홍박사"),
            CounseleeFixture.create("홍세달"),
            CounseleeFixture.create("백홍준"),
            CounseleeFixture.create("김지홍"));

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