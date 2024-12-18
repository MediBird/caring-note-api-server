package com.springboot.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.enums.ScheduleStatus;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
class CounselSessionRepositoryTest {

    @Autowired
    private CounselSessionRepository counselSessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and retrieve a CounselSession by ID")
    @Sql("/sql/test-counselor.sql")
    @Sql("/sql/test-counselee.sql")
    void testSaveAndFindById() {

        Counselee counselee = entityManager.find(Counselee.class,"TEST-COUNSELEE-01");
        Counselor counselor = entityManager.find(Counselor.class,"TEST-COUNSELOR-01");

        CounselSession counselSession = CounselSession.builder()
                .status(ScheduleStatus.SCHEDULED)
                .counselee(counselee) // 실제 영속 상태의 엔티티 사용
                .counselor(counselor) // 실제 영속 상태의 엔티티 사용
                .scheduledStartDateTime(LocalDateTime.of(2024, 12, 10, 10, 0, 0))
                .build();

        CounselSession savedSession = counselSessionRepository.save(counselSession);
        Optional<CounselSession> retrievedSession = counselSessionRepository.findById(savedSession.getId());

        assertThat(savedSession).isNotNull();
        assertThat(retrievedSession).isPresent();
        assertThat(retrievedSession.get().getId()).isEqualTo(savedSession.getId());
    }

    @Test
    @DisplayName("Should delete a CounselSession by ID")
    @Sql("/sql/test-counselor.sql")
    @Sql("/sql/test-counselee.sql")
    @Sql("/sql/test-counsel-session.sql")
    void testDeleteById() {

        String counselSessionId = "TEST-COUNSEL-SESSION-01";

        counselSessionRepository.deleteById(counselSessionId);
        Optional<CounselSession> deletedSession = counselSessionRepository.findById(counselSessionId);
        assertThat(deletedSession).isEmpty();
    }

    @Test
    @DisplayName("Should find CounselSessions using cursor-based pagination")
    @Sql("/sql/test-counselor.sql")
    @Sql("/sql/test-counselee.sql")
    @Sql("/sql/test-counsel-session.sql")
    void testFindByCursor() {


        List<CounselSession> sessions = counselSessionRepository.findByCursor(
                null
                ,null // cursorScheduledStartDateTime
                ,null             // cursorId
                ,null           // counselorId
                ,PageRequest.of(0, 1) // pageable
        );

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getId()).isEqualTo("TEST-COUNSEL-SESSION-03");
    }
}

