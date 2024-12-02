package com.springboot.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.springboot.api.domain.Counselor;

@DataJpaTest
class CounselorRepositoryTest {

    @Autowired
    private CounselorRepository counselorRepository;

    private Counselor createCounselor(String id, String name, String email) {
        Counselor counselor = new Counselor();
        counselor.setId(id);
        counselor.setName(name);
        counselor.setEmail(email);
        return counselorRepository.save(counselor);
    }

    @Test
    @DisplayName("Should save a counselor successfully")
    void testSaveCounselor() {
        Counselor counselor = createCounselor("1", "John Doe", "john.doe@example.com");
        assertThat(counselor).isNotNull();
        assertThat(counselor.getId()).isEqualTo("1");
        assertThat(counselor.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should find a counselor by ID")
    void testFindById() {
        createCounselor("2", "Jane Smith", "jane.smith@example.com");
        Optional<Counselor> foundCounselor = counselorRepository.findById("2");
        assertThat(foundCounselor).isPresent();
        assertThat(foundCounselor.get().getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should check existence by email")
    void testExistsByEmail() {
        createCounselor("3", "Alice Johnson", "alice.johnson@example.com");
        boolean exists = counselorRepository.existsByEmail("alice.johnson@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find a counselor by email")
    void testFindByEmail() {
        createCounselor("4", "Bob Brown", "bob.brown@example.com");
        Optional<Counselor> foundCounselor = counselorRepository.findByEmail("bob.brown@example.com");
        assertThat(foundCounselor).isPresent();
        assertThat(foundCounselor.get().getId()).isEqualTo("4");
    }
}
