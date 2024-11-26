package com.springboot.api.repository;

import com.springboot.api.domain.CounselSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CounselSessionRepository extends JpaRepository<CounselSession, String> {

    Optional<CounselSession> findById(String id);
    
    CounselSession save(CounselSession counselSession);


    @Query("""
        SELECT cs FROM CounselSession cs
        WHERE (:cursorScheduledStartDateTime IS NULL OR cs.scheduledStartDateTime > :cursorScheduledStartDateTime)
          AND (:cursorId IS NULL OR cs.id > :cursorId)
          AND (cs.counselor.id IS NULL OR cs.counselor.id = :counselorId)
        ORDER BY cs.id DESC
        """)
    List<CounselSession> findByCursor(
            @Param("cursorScheduledStartDateTime") LocalDateTime cursorScheduledStartDateTime,
            @Param("cursorId") String cursorId,
            @Param("counselorId") String counselorId,
            Pageable pageable
    );
}
