package com.springboot.api.repository;


import com.springboot.api.domain.CounselCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselCardRepository extends JpaRepository<CounselCard, String> {


    CounselCard save(CounselCard counselCard);

    Optional<CounselCard> findById(String id);

    void deleteById(String id);


}