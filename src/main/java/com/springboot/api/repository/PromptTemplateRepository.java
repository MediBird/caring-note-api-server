package com.springboot.api.repository;

import com.springboot.api.domain.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, String> {

}
