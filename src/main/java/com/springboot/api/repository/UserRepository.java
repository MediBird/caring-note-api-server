package com.springboot.api.repository;

import com.springboot.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // username을 기준으로 사용자 정보를 조회
    Optional<User> findByEmail(String email);
}