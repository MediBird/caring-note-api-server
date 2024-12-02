package com.springboot.api.repository;

import com.springboot.api.domain.Counselee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounseleeRepository extends JpaRepository<Counselee, String> {


    Optional<Counselee> findById(String id);


}
