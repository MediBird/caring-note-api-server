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

    List<CounselSession> findByCounseleeIdAndScheduledStartDateTimeLessThan(String counseleeId,
        LocalDateTime scheduledStartDateTime);

    boolean existsByCounselorAndScheduledStartDateTime(Counselor counselor, LocalDateTime scheduledStartDateTime);

    boolean existsByCounseleeAndScheduledStartDateTime(Counselee counselee, LocalDateTime scheduledStartDateTime);
}
