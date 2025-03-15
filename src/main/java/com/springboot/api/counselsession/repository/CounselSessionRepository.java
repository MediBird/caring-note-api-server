package com.springboot.api.counselsession.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.entity.CounselSession;

public interface CounselSessionRepository
                extends JpaRepository<CounselSession, String>, CounselSessionRepositoryCustom {

        @Override
        void deleteById(@NonNull String id);

        @Query("""
                        SELECT cs FROM CounselSession cs
                        WHERE cs.counselee.id = :counseleeId
                         AND cs.id < :id
                        ORDER BY cs.id DESC
                        """)
        List<CounselSession> findByCounseleeIdPrevious(
                        @Param("counseleeId") String counseleeId, @Param("id") String id, Pageable pageable);

        List<CounselSession> findByCounseleeIdAndScheduledStartDateTimeLessThan(String counseleeId,
                        LocalDateTime scheduledStartDateTime);

        boolean existsByCounselorAndScheduledStartDateTime(Counselor counselor, LocalDateTime scheduledStartDateTime);

        boolean existsByCounseleeAndScheduledStartDateTime(Counselee counselee, LocalDateTime scheduledStartDateTime);

        // 상담사 ID로 상담 세션 찾기
        List<CounselSession> findByCounselorId(String counselorId);
}
