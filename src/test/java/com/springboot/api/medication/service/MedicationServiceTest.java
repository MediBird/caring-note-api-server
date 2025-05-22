package com.springboot.api.medication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.springboot.api.medication.dto.SearchMedicationByKeywordRes;
import com.springboot.api.medication.entity.Medication;
import com.springboot.api.medication.repository.MedicationRepository;
import de.huxhorn.sulky.ulid.ULID;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {

    private static final ULID ulid = new ULID();
    @Mock
    private MedicationRepository medicationRepository;
    @InjectMocks
    private MedicationService medicationService;

    @Test
    public void testSearchMedicationsByName() {
        // given
        String keyword = "타이레놀";
        System.out.println("Converting to Chosung: " + keyword);
        String ulid1 = ulid.nextULID();
        String ulid2 = ulid.nextULID();
        List<SearchMedicationByKeywordRes> expectedResults = Arrays.asList(
            SearchMedicationByKeywordRes.builder().id(ulid1).itemName("타이레놀")
                .itemImage("image1.jpg")
                .build(),
            SearchMedicationByKeywordRes.builder().id(ulid2).itemName("타이레놀 500mg")
                .itemImage("image2.jpg")
                .build());

        Medication medication1 = Medication.builder()
            .id(ulid1)
            .itemName("타이레놀")
            .itemImage("image1.jpg")
            .build();

        Medication medication2 = Medication.builder()
            .id(ulid2)
            .itemName("타이레놀 500mg")
            .itemImage("image2.jpg")
            .build();

        List<Medication> medications = List.of(
            medication1,
            medication2);
        when(medicationRepository.searchByKeyword(keyword)).thenReturn(medications);

        // when
        List<SearchMedicationByKeywordRes> actualResults = medicationService.searchMedicationByKeyword(keyword);
        // then
        assertEquals(expectedResults, actualResults);
    }


}
