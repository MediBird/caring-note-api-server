package com.springboot.api.repository;

import com.springboot.api.domain.CounselSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CounselSessionRepository extends JpaRepository<CounselSession, String> {


    void deleteById(String id);

    @Query("""
        SELECT cs FROM CounselSession cs
        WHERE (:startOfDay IS NULL OR cs.scheduledStartDateTime >= :startOfDay)
          AND (:endOfDay IS NULL OR cs.scheduledStartDateTime < :endOfDay)
          AND (:cursorId IS NULL OR cs.id > :cursorId)
          AND (:counselorId IS NULL OR cs.counselor.id = :counselorId)
        ORDER BY cs.id DESC
        """)
    List<CounselSession> findByCursor(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("cursorId") String cursorId,
            @Param("counselorId") String counselorId,
            Pageable pageable
    );

    @Query("""
        SELECT cs FROM CounselSession cs
        WHERE cs.counselee.id = :counseleeId
         AND cs.id < :id
        ORDER BY cs.id DESC
        """)
    List<CounselSession> findByCounseleeIdPrevious(
            @Param("counseleeId") String counseleeId
            ,@Param("id") String id
            ,Pageable pageable
    );



}
