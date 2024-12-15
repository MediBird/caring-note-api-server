package com.springboot.api.repository;

import com.springboot.api.domain.CounselCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselCardRepository extends JpaRepository<CounselCard, String> {


}
