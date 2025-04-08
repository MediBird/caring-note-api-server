package com.springboot.api.medication.repository;

import java.util.List;
import java.util.regex.Pattern;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.medication.entity.Medication;
import com.springboot.api.medication.entity.QMedication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MedicationRepositoryCustomImpl implements MedicationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QMedication medication = QMedication.medication;

    private static final Pattern CHOSUNG_PATTERN = Pattern.compile("^[ㄱ-ㅎ\\s]+$");

    @Override
    public List<Medication> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        String trimmedKeyword = keyword.trim();

        // 초성인지 일반 텍스트인지 확인
        if (isChosung(trimmedKeyword)) {
            // 초성 검색 로직
            return searchByChosung(trimmedKeyword);
        } else {
            // 일반 텍스트 검색 로직
            return searchByName(trimmedKeyword);
        }
    }

    @Override
    public boolean isChosung(String keyword) {
        // 키워드가 한글 초성으로만 이루어졌는지 확인
        return CHOSUNG_PATTERN.matcher(keyword).matches();
    }

    private List<Medication> searchByChosung(String keyword) {
        return queryFactory
                .selectFrom(medication)
                .where(medication.itemNameChosung.like("%" + keyword + "%"))
                .orderBy(new CaseBuilder()
                        .when(medication.itemNameChosung.startsWith(keyword)).then(1)
                        .when(medication.itemNameChosung.contains(keyword)).then(2)
                        .otherwise(3)
                        .asc())
                .limit(10)
                .fetch();
    }

    private List<Medication> searchByName(String keyword) {
        log.info("일반 텍스트 검색: {}", keyword);

        return queryFactory
                .selectFrom(medication)
                .where(nameContains(keyword))
                .orderBy(new CaseBuilder()
                        .when(medication.itemName.startsWith(keyword)).then(1)
                        .when(medication.itemName.contains(keyword)).then(2)
                        .otherwise(3)
                        .asc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression nameContains(String keyword) {
        return keyword != null ? medication.itemName.containsIgnoreCase(keyword) : null;
    }
}