package com.springboot.api.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.springboot.api.domain.Medication;
import com.springboot.api.dto.medication.MedicationAutoCompleteDTO;
import com.springboot.api.repository.MedicationRepository;

import de.huxhorn.sulky.ulid.ULID;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationService medicationService;

    private static final ULID ulid = new ULID();


    @Test
    public void testSearchMedicationsByName() {
        // given
        String keyword = "타이레놀";
        System.out.println("Converting to Chosung: " + keyword);
        String ulid1 = ulid.nextULID();
        String ulid2 = ulid.nextULID();
        List<MedicationAutoCompleteDTO> expectedResults = Arrays.asList(
            MedicationAutoCompleteDTO.builder().id(ulid1).itemName("타이레놀").itemImage("image1.jpg").build(),
            MedicationAutoCompleteDTO.builder().id(ulid2).itemName("타이레놀 500mg").itemImage("image2.jpg").build()
        );

        Medication medication1 = new Medication();
        medication1.setId(ulid1);
        medication1.setItemName("타이레놀");
        medication1.setItemImage("image1.jpg");
        
        Medication medication2 = new Medication();
        medication2.setId(ulid2);
        medication2.setItemName("타이레놀 500mg");
        medication2.setItemImage("image2.jpg");
        List<Medication> medications = List.of(
            medication1,
            medication2
        );
        when(medicationRepository.searchByItemNameWithPattern(keyword, "ㅌㅇㄹㄴ")).thenReturn(medications);

        // when
        List<MedicationAutoCompleteDTO> actualResults = medicationService.searchMedicationsByName(keyword);
        // then
        assertEquals(expectedResults, actualResults);
    }

    @Test
    public void testConvertToChosung() {
        // given
        String keyword = "타이레놀";
        
        // when
        String pattern = medicationService.convertToChosung(keyword);
        // then
        assertEquals("ㅌㅇㄹㄴ", pattern);
        assertNotEquals(keyword, pattern);
    }
}
