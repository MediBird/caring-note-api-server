package com.springboot.api.counselee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselee.entity.Counselee;

public interface CounseleeRepository extends JpaRepository<Counselee, String>, CounseleeRepositoryCustom {

    boolean existsByPhoneNumber(String phoneNumber);
}
