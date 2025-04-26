package com.springboot.api.medication.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.springboot.api.medication.dto.SearchMedicationByKeywordRes;
import com.springboot.api.medication.repository.MedicationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MedicationService {

    private final MedicationRepository medicationRepository;

    @Cacheable(value = "medicationSearch")
    public List<SearchMedicationByKeywordRes> searchMedicationByKeyword(String keyword) {
        log.info("검색 키워드: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        return medicationRepository.searchByKeyword(keyword.trim())
            .stream()
            .map(medication -> SearchMedicationByKeywordRes.builder()
                .id(medication.getId())
                .itemName(medication.getItemName())
                .itemImage(medication.getItemImage())
                .build())
            .collect(Collectors.toList());
    }
}
