package com.springboot.api.service;

import com.springboot.api.dto.medication.SearchMedicationsByNameRes;
import com.springboot.api.repository.MedicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MedicationService {
    private final MedicationRepository medicationRepository;
    
    @Cacheable(value = "medicationSearch")
    public List<SearchMedicationsByNameRes> searchMedicationsByName(String keyword) {
        log.info("keyword: {}", keyword);   
        return medicationRepository.searchByItemNameWithPattern(
                keyword,
                createSearchPattern(keyword)
            )
            .stream()
            .limit(10)
            .map(medication -> SearchMedicationsByNameRes.builder()
                .id(medication.getId())
                .itemName(medication.getItemName())
                .itemImage(medication.getItemImage())
                .build())
            .collect(Collectors.toList());
    }

    public String createSearchPattern(String keyword){
        return convertToChosung(keyword);
    }
    
    public String convertToChosung(String text) {
        String[] chosung = {"ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ","ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ"};
        StringBuilder result = new StringBuilder();
        
        for (char ch : text.toCharArray()) {
            if (ch >= '가' && ch <= '힣') {
                int unicode = ch - '가';
                int cho = unicode / (21 * 28);
                result.append(chosung[cho]);
            } else {
                result.append(ch);
            }
        }
        log.info(result.toString());
        return result.toString();
    }
}
