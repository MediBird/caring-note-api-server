package com.springboot.api.repository;

import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
@Sql("/sql/test-role.sql")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("권한 정보 조회 테스트")
    public void testFindByNameIn() {
        List<String> roleNames = List.of("ADMIN", "USER");

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        System.out.println("Roles: " + roles);
        assertEquals(2, roles.size());
    }
}
