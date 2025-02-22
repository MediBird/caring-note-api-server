package com.springboot.api.repository;

import com.springboot.api.domain.CounselCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CounselCardRepository extends JpaRepository<CounselCard, String> {

    @Query("""
            SELECT cd FROM CounselCard cd
            WHERE cd.counselSession.counselee.id = :counseleeId
            AND cd.cardRecordStatus = 'RECORDED'
            ORDER BY cd.counselSession.scheduledStartDateTime DESC
            LIMIT 1
            """)
    Optional<CounselCard> findLastRecordedCounselCard(String counseleeId);
}
