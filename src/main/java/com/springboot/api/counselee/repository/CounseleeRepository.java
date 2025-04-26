package com.springboot.api.counselee.repository;

import com.springboot.api.counselee.entity.Counselee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounseleeRepository extends JpaRepository<Counselee, String>, CounseleeRepositoryCustom {

}
