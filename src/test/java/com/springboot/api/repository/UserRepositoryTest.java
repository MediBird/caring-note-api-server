package com.springboot.api.repository;

import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("email로 회원 정보 조회 테스트")
    @Sql("/sql/test-user.sql") // 테스트 데이터 삽입을 위한 SQL 파일
    public void testFindByEmail() {
        String email = "example1@gmail.com";
        Optional<User> user = userRepository.findByEmail(email);
        assertTrue(user.isPresent());
    }
}
