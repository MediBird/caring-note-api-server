package com.springboot.api.counselcard.repository;

import java.util.Optional;

import com.springboot.api.counselcard.entity.CounselCard;

public interface CounselCardRepositoryCustom {

    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);
}