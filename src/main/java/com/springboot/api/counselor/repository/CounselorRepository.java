package com.springboot.api.counselor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselor.entity.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, String>, CounselorRepositoryCustom {
}
