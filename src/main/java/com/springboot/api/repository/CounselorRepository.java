package com.springboot.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.Counselor;

public interface CounselorRepository extends JpaRepository<Counselor, String> {

    boolean existsByEmail(String Email);

    Optional<Counselor> findByEmail(String email);

    Optional<Counselor> findByUsername(String username);

}
