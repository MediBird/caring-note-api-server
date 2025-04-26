package com.springboot.api.counselcard.repository;

import java.util.List;
import java.util.Optional;

import com.springboot.api.counselcard.entity.CounselCard;

public interface CounselCardRepositoryCustom {

    Optional<CounselCard> findCounselCardWithCounselee(String counselSessionId);

    Optional<CounselCard> findCounselCardByCounselSessionId(String counselSessionId);

    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);

    List<CounselCard> findRecordedCardsByPreviousSessions(String currentSessionId);
}