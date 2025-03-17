package com.springboot.api.counselor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselor.entity.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, String>, CounselorRepositoryCustom {

    boolean existsByEmail(String Email);

    Optional<Counselor> findByEmail(String email);

    Optional<Counselor> findByUsername(String username);

}
