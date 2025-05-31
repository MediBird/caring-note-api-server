package com.springboot.api.counselcard.repository;

import com.springboot.api.counselcard.entity.CounselCard;
import java.util.List;
import java.util.Optional;

public interface CounselCardRepositoryCustom {

    Optional<CounselCard> findCounselCardWithCounselee(String counselSessionId);

    Optional<CounselCard> findCounselCardByCounselSessionId(String counselSessionId);

    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);

    List<CounselCard> findRecordedCardsByPreviousSessions(String currentSessionId);

    List<CounselCard> findCurrentAndPastCounselCardsBySessionId(String counselSessionId);
}