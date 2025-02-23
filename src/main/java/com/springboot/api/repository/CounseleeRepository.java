package com.springboot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.Counselee;

public interface CounseleeRepository extends JpaRepository<Counselee, String>, CounseleeRepositoryCustom {
    boolean existsByPhoneNumber(String phoneNumber);

}
