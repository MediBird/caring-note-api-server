package com.springboot.api.repository;

import com.springboot.api.domain.Role;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName(" 권한 정보 조회 테스트")
    public void testFindByNameIn() {

        List<String> ruleNames = List.of("ADMIN", "USER");

        List<Role> roles = roleRepository.findByNameIn(ruleNames);
        System.out.println("Roles: " + roles);
        assertEquals(2, roles.size());

    }
}
