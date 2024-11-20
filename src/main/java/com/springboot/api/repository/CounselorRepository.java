package com.springboot.api.repository;


import com.springboot.api.domain.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselorRepository extends JpaRepository<Counselor, String> {

    Optional<Counselor> findById(String id);

    boolean existsByEmail(String Email);

    Counselor save(Counselor counselor);

}
