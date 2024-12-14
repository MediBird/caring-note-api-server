package com.springboot.api.repository;

import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
class CounselCardRepositoryTest {

    @Autowired
    private CounselCardRepository counselCardRepository;

    @Autowired
    private CounselSessionRepository counselSessionRepository;

    @Test
    @DisplayName("Should save a CounselCard successfully")
    void testSaveCounselCard() {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSession = counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
                .baseInformation(Map.of("name", "John Doe", "age", 30))
                .healthInformation(Map.of("height", 180, "weight", 75))
                .livingInformation(Map.of("address", "123 Test St"))
                .selfReliantLivingInformation(Map.of("independent", true))
                .build();

        // When
        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        // Then
        assertThat(savedCounselCard).isNotNull();
        assertThat(savedCounselCard.getId()).isNotNull();
        assertThat(savedCounselCard.getBaseInformation().get("name")).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find a CounselCard by ID")
    void testFindById() {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
                .baseInformation(Map.of("name", "Jane Doe", "age", 28))
                .build();

        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        // When
        Optional<CounselCard> foundCounselCard = counselCardRepository.findById(savedCounselCard.getId());

        // Then
        assertThat(foundCounselCard).isPresent();
        assertThat(foundCounselCard.get().getBaseInformation().get("name")).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should delete a CounselCard by ID")
    void testDeleteById() {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
                .baseInformation(Map.of("name", "Delete Test"))
                .build();

        CounselCard savedCounselCard = counselCardRepository.save(counselCard);
        String id = savedCounselCard.getId();

        // When
        counselCardRepository.deleteById(id);
        Optional<CounselCard> deletedCounselCard = counselCardRepository.findById(id);

        // Then
        assertThat(deletedCounselCard).isEmpty();
    }
}
