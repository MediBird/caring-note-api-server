package com.springboot.api.repository;
import com.springboot.api.domain.CounselSession;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselSessionRepository extends JpaRepository<CounselSession, String> {

    Optional<CounselSession> findById(String id);
    
    CounselSession save(CounselSession counselSession);

}
