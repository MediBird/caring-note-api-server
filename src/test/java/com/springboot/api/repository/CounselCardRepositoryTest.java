package com.springboot.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.config.JpaTestAdditionalConfig;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.counselcard.information.base.BaseInformationDTO;

@DataJpaTest
@Import(JpaTestAdditionalConfig.class)
class CounselCardRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(CounselCardRepositoryTest.class);
    @Autowired
    private CounselCardRepository counselCardRepository;

    @Autowired
    private CounselSessionRepository counselSessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should save a CounselCard successfully")
    void testSaveCounselCard() throws JsonProcessingException {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSession = counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
                .baseInformation(objectMapper.readTree("""
                                   {
                            "version": "1.0",
                            "baseInfo": {
                                "counseleeId": "string",
                                "name": "김늘픔",
                                "birthDate": "1995-07-19",
                                "counselSessionOrder": "1회차",
                                "lastCounselDate": "2024-12-20",
                                "healthInsuranceType": "VETERANS"
                            },
                            "counselPurposeAndMomo": {
                                "counselPurpose": ["약물 부작용 상담", "약물 복용 관련 상담"],
                                "SignificantNote": "특이사항",
                                "MedicationNote": "복약 관련 메모"
                            }
                        }
                        """))
                .healthInformation(objectMapper.readTree("""
                                           {
                            "version": 1.0,
                            "diseaseInfo": {
                                "diseases": ["고혈압", "고지혈증"],
                                "historyNote": "고혈압, 당뇨, 고관절, 염증 수술",
                                "mainInconvenienceNote": "고관절 통증으로 걷기가 힘듦"
                            },
                            "allergy": {
                                "isAllergy": true,
                                "allergyNote": "땅콩, 돼지고기"
                            },
                            "medicationSideEffect": {
                                "isSideEffect": true,
                                "suspectedMedicationNote": "타미플루",
                                "symptomsNote": "온 몸이 붓고, 특히 얼굴이 가렵고 붉어짐"
                            }
                        }
                        """))
                .livingInformation(objectMapper.readTree("""
                                           {
                            "version": 1.0,
                            "smoking": {
                                "isSmoking": true,
                                "smokingPeriodNote": "10년 02개월",
                                "smokingAmount": "1갑"
                            },
                            "drinking": {
                                "isDrinking": true,
                                "drinkingAmount": "1회"
                            },
                            "nutrition": {
                                "mealPattern": "하루 한 끼 규칙적 식사",
                                "nutritionNote": "잇몸 문제로 딱딱한 음식 섭취 어려움"
                            },
                            "exercise": {
                                "exercisePattern": "1회",
                                "exerciseNote": "유산소 운동"
                            },
                            "medicationManagement": {
                                "isAlone": true,
                                "houseMateNote": "아들, 딸",
                                "medicationAssistants": ["본인", "배우자", "자녀", "본인"]
                            }
                        }
                        """))
                .independentLifeInformation(objectMapper.readTree("""
                                            {
                             "version": 1.0,
                             "walking": {
                                 "walkingMethods": ["와상 및 보행불가", "자립보행 가능"],
                                 "walkingEquipments": ["지팡이", "워커"],
                                 "etcNote": ""
                             },
                             "evacuation": {
                                 "evacuationMethods": ["자립 화장실 사용", "화장실 유도"],
                                 "etcNote": ""
                             },
                             "Communication": {
                                 "visibles": ["잘보임", "잘안보임", "안보임", "안경 사용"],
                                 "auditables": ["잘들림", "잘안들림", "안들림", "보청기 사용"],
                                 "Communications": ["소통 가능함", "대강 가능함", "불가능"],
                                 "Usingkoreans": ["읽기 가능", "쓰기 가능"]
                             }
                         }
                        """))
                .build();

        // When
        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        // Then
        assertThat(savedCounselCard).isNotNull();
        assertThat(savedCounselCard.getId()).isNotNull();
        assertThat(savedCounselCard.getBaseInformation().get("version").asDouble()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Should find a CounselCard by ID")
    void testFindById() throws JsonProcessingException {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
                .baseInformation(objectMapper.readTree("""
                                   {
                            "version": 1.0,
                            "baseInfo": {
                                "counseleeId": "string",
                                "name": "김늘픔",
                                "birthDate": "1995-07-19",
                                "counselSessionOrder": "1회차",
                                "lastCounselDate": "2024-12-20",
                                "healthInsuranceType": "VETERANS_BENEFITS"
                            },
                            "counselPurposeAndNote": {
                                "counselPurpose": ["약물 부작용 상담", "약물 복용 관련 상담"],
                                "SignificantNote": "특이사항",
                                "MedicationNote": "복약 관련 메모"
                            }
                        }
                        """))
                .build();

        CounselCard savedCounselCard = counselCardRepository.save(counselCard);

        // When
        Optional<CounselCard> foundCounselCard = counselCardRepository.findById(savedCounselCard.getId());

        // Then
        assertThat(foundCounselCard).isPresent();
        assertThat(foundCounselCard.get().getBaseInformation().get("baseInfo").get("name").asText()).isEqualTo("김늘픔");

        BaseInformationDTO baseInformationDTO = objectMapper.treeToValue(foundCounselCard.get().getBaseInformation(),
                BaseInformationDTO.class);
        log.debug(baseInformationDTO.toString());

    }

    @Test
    @DisplayName("Should delete a CounselCard by ID")
    void testDeleteById() {
        // Given
        CounselSession counselSession = new CounselSession();
        counselSessionRepository.save(counselSession);

        CounselCard counselCard = CounselCard.builder()
                .counselSession(counselSession)
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
