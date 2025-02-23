package com.springboot.api.repository;

import com.springboot.api.domain.CounselCard;
import java.util.Optional;

public interface CounselCardRepositoryCustom {

    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);
}