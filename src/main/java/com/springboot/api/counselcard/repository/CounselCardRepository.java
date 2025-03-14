package com.springboot.api.counselcard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselcard.entity.CounselCard;

public interface CounselCardRepository extends JpaRepository<CounselCard, String>, CounselCardRepositoryCustom {
    boolean existsByCounselSessionId(String counselSessionId);
}
