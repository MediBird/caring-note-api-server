package com.springboot.api.tus.repository;

import com.springboot.api.tus.entity.SessionRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRecordRepository extends JpaRepository<SessionRecord, String> {

    Optional<SessionRecord> findByCounselSessionId(String sessionId);
}
