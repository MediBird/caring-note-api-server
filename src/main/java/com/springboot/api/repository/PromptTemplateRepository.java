package com.springboot.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.PromptTemplate;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, String> {

}
