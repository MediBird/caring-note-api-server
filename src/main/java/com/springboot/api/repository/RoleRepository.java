package com.springboot.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.api.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(List<String> names);

}
