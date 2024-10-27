package com.springboot.api.repository;

import com.springboot.api.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository  extends JpaRepository<Role, Long> {

    List<Role> findByNameIn(List<String> names);

}
