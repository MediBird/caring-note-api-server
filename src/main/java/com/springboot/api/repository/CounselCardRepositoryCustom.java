package com.springboot.api.repository;

import java.util.Optional;

import com.springboot.api.domain.CounselCard;

public interface CounselCardRepositoryCustom {

    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);
}