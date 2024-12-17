package com.springboot.api.repository;

import com.springboot.api.domain.Counselee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounseleeRepository extends JpaRepository<Counselee, String> {


}
