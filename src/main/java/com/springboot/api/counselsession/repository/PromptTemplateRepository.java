package com.springboot.api.counselsession.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.counselsession.entity.PromptTemplate;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, String> {

}
