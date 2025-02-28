package com.springboot.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.CounselCard;

public interface CounselCardRepository extends JpaRepository<CounselCard, String>, CounselCardRepositoryCustom {
    Optional<CounselCard> findByCounselSessionId(String counselSessionId);
}
