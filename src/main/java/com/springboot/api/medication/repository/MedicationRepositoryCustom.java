package com.springboot.api.medication.repository;

import java.util.List;

import com.springboot.api.medication.entity.Medication;

public interface MedicationRepositoryCustom {

    /**
     * 키워드를 기반으로 약물을 검색합니다.
     * 키워드가 초성인 경우 초성 검색을 수행하고, 일반 텍스트인 경우 이름 기반 검색을 수행합니다.
     * 
     * @param keyword 검색 키워드
     * @return 검색 결과 약물 목록
     */
    List<Medication> searchByKeyword(String keyword);

    /**
     * 키워드가 초성인지 확인합니다.
     * 
     * @param keyword 검색 키워드
     * @return 초성 여부
     */
    boolean isChosung(String keyword);
}