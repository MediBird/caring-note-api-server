package com.springboot.api.common.config.initializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.domain.*;
import com.springboot.enums.*;
import com.springboot.enums.wasteMedication.DrugRemainActionType;
import com.springboot.enums.wasteMedication.RecoveryAgreementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final List<String> names = List.of(
            "김철수", "김바비", "김을동", "박찬수", "장덕구", "임꺽정", "송사리", "송새벽", "한여름", "오로라", "이루리"
    );
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (Arrays.asList(args).contains("--initTestData")) {
            initTestData();
        }
    }

    private void initTestData() throws JsonProcessingException {
        // add Counselor
        String counselorId = "TEST-COUNSELOR-01";
        addCounselor(counselorId);

        // add Counselee
        List<String> counseleeIds = List.of("TEST-COUNSELEE-01", "TEST-COUNSELEE-02");

        IntStream.range(0, counseleeIds.size())
                .forEach(index -> addCounselee(counseleeIds.get(index), index % 2 == 0));

        // add CounselSession
        List<String> counselSessionIds = List.of("TEST-COUNSEL-SESSION-01",
                "TEST-COUNSEL-SESSION-02",
                "TEST-COUNSEL-SESSION-03");

        addCounselSession(counselSessionIds.getFirst(), counselorId, counseleeIds.getFirst(), ScheduleStatus.COMPLETED);
        addCounselSession(counselSessionIds.get(1), counselorId, counseleeIds.getFirst(), ScheduleStatus.SCHEDULED);
        addCounselSession(counselSessionIds.get(2), counselorId, counseleeIds.getLast(), ScheduleStatus.SCHEDULED);

        // add CounselCard
        List<String> counselCardIds = List.of(
                "TEST-COUNSEL-CARD-01"
        );
        addCounselCard(counselSessionIds.getFirst(), counselCardIds.getFirst(), counseleeIds.getFirst());

        // add CounseleeConsent
        List<String> counseleeConsentIds = List.of(
                "TEST-COUNSEL-CONSENT-01"
        );
        addCounseleeConsent(counselSessionIds.getFirst(),
                counseleeConsentIds.getFirst(),
                counseleeIds.getFirst());

        // add MedicationCounsel
        List<String> medicationCounselIds = List.of(
                "TEST-COUNSEL-MEDICATION-01"
        );
        addMedicationCounsel(counselSessionIds.getFirst(),
                medicationCounselIds.getFirst());

        // add MedicationRecordHist
        List<String> medicationRecordHistIds = List.of(
                "TEST-RECORD-HIST-01",
                "TEST-RECORD-HIST-02",
                "TEST-RECORD-HIST-03",
                "TEST-RECORD-HIST-04",
                "TEST-RECORD-HIST-05",
                "TEST-RECORD-HIST-06",
                "TEST-RECORD-HIST-07",
                "TEST-RECORD-HIST-08",
                "TEST-RECORD-HIST-09",
                "TEST-RECORD-HIST-10"
        );
        addMedicationRecordHist(counselSessionIds.getFirst(), medicationRecordHistIds);

        // add WasteMedicationDisposal
        List<String> wasteMedicationDisposalIds = List.of("TEST-WASTE-DISPOSAL-01");
        addWasteMedicationDisposal(counselSessionIds.getFirst(), wasteMedicationDisposalIds.getFirst());

        // add WasteMedicationRecord
        List<String> wasteMedicationRecordIds = List.of(
                "TEST-WASTE-RECORD-HIST-01",
                "TEST-WASTE-RECORD-HIST-02",
                "TEST-WASTE-RECORD-HIST-03",
                "TEST-WASTE-RECORD-HIST-04",
                "TEST-WASTE-RECORD-HIST-05",
                "TEST-WASTE-RECORD-HIST-06",
                "TEST-WASTE-RECORD-HIST-07",
                "TEST-WASTE-RECORD-HIST-08",
                "TEST-WASTE-RECORD-HIST-09",
                "TEST-WASTE-RECORD-HIST-10"
        );
        addWasteMedicationRecord(counselSessionIds.getFirst(), wasteMedicationRecordIds);

    }

    private void addCounselor(String counselorId) {

        if (entityManager.find(Counselor.class, counselorId) == null) {

            Counselor counselor = Counselor
                    .builder()
                    .email(counselorId + "@gmail.com")
                    .phoneNumber(getRandomPhoneNumber())
                    .name(names.get(random.nextInt(names.size())))
                    .password(passwordEncoder.encode("1234qwer!@"))
                    .roleType(RoleType.ROLE_ADMIN)
                    .status(CounselorStatus.ACTIVE)
                    .registrationDate(LocalDate.now())
                    .build();

            counselor.setId(counselorId);

            entityManager.persist(counselor);

        }

    }

    private void addCounselee(String counseleeId, boolean isDisability) {

        if (entityManager.find(Counselee.class, counseleeId) == null) {
            Counselee counselee = Counselee
                    .builder()
                    .name(names.get(random.nextInt(names.size())))
                    .dateOfBirth(getRandomDate("1930-01-01", "2000-01-01"))
                    .genderType(GenderType.MALE)
                    .isDisability(isDisability)
                    .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
                    .phoneNumber(getRandomPhoneNumber())
                    .registrationDate(LocalDate.now())
                    .build();

            counselee.setId(counseleeId);

            entityManager.persist(counselee);
        }
    }

    private void addCounselSession(String counselSessionId,
            String counselorId,
            String counseleeId,
            ScheduleStatus scheduleStatus) {

        Counselor counselor = entityManager.getReference(Counselor.class, counselorId);
        Counselee counselee = entityManager.getReference(Counselee.class, counseleeId);
        LocalDate scheduleDate
                = scheduleStatus == ScheduleStatus.SCHEDULED
                        ? LocalDate.now()
                        : getRandomDate("2024-12-01", LocalDate.now().toString());

        LocalDateTime scheduleDateTime = scheduleDate.atTime(random.nextInt(9, 19), 0);
        ;

        if (entityManager.find(CounselSession.class, counselSessionId) == null) {
            CounselSession counselSession = CounselSession
                    .builder()
                    .counselor(counselor)
                    .counselee(counselee)
                    .status(scheduleStatus)
                    .scheduledStartDateTime(scheduleDateTime)
                    .startDateTime(scheduleDateTime)
                    .endDateTime(scheduleDateTime.plusMinutes(30))
                    .build();

            counselSession.setId(counselSessionId);

            entityManager.persist(counselSession);
        }
    }

    private void addCounselCard(String counselSessionId, String counselCardId, String counseleeId) throws JsonProcessingException {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);
        Counselee counselee = entityManager.getReference(Counselee.class, counseleeId);

        JsonNode baseInformation = objectMapper.readTree(String.format("""
                 {
                                  "version": "1.0",
                                  "baseInfo": {
                                      "counseleeId": "%s",
                                      "name": "%s",
                                      "birthDate": "%s",
                                      "counselSessionOrder": "1회차",
                                     "lastCounselDate": "",
                                     "healthInsuranceType": "MEDICAL_AID",
                                  },
                                  "counselPurposeAndNote": {
                                      "counselPurpose": ["약물 부작용 상담", "약물 복용 관련 상담"],
                                      "SignificantNote": "특이사항",
                                      "MedicationNote": "복약 관련 메모"
                                  }
                 }
                 """, counselee.getId(), counselee.getName(), counselee.getDateOfBirth().toString()));

        JsonNode healthInformation = objectMapper.readTree("""
                 {
                                  "version": "1.0",
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
                 """
        );

        JsonNode livingInformation = objectMapper.readTree("""
                {
                                 "version": "1.0",
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
                """);

        JsonNode independentLifeInformation = counselee.isDisability() ? objectMapper.readTree("""
                {
                                 "version": "1.0",
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
                """)
                : null;

        if (entityManager.find(CounselCard.class, counselCardId) == null) {
            CounselCard counselCard = CounselCard
                    .builder()
                    .counselSession(counselSession)
                    .baseInformation(baseInformation)
                    .healthInformation(healthInformation)
                    .livingInformation(livingInformation)
                    .independentLifeInformation(independentLifeInformation)
                    .cardRecordStatus(counselSession.getStatus() == ScheduleStatus.CANCELED
                            ? CardRecordStatus.RECORDED : CardRecordStatus.RECORDING)
                    .build();

            counselCard.setId(counselCardId);

            entityManager.persist(counselCard);
        }

    }

    private void addCounseleeConsent(String counselSessionId, String counseleeConsentId, String counseleeId) {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);
        Counselee counselee = entityManager.getReference(Counselee.class, counseleeId);

        if (entityManager.find(CounseleeConsent.class, counseleeConsentId) == null) {
            CounseleeConsent counseleeConsent = CounseleeConsent.builder()
                    .isConsent(true)
                    .counselSession(counselSession)
                    .counselee(counselee)
                    .consentDateTime(counselSession.getStartDateTime())
                    .build();
            counseleeConsent.setId(counseleeConsentId);

            entityManager.persist(counseleeConsent);
        }

    }

    private void addMedicationCounsel(String counselSessionId, String medicationCounselId) {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);

        if (entityManager.find(MedicationCounsel.class, medicationCounselId) == null) {
            MedicationCounsel medicationCounsel = MedicationCounsel
                    .builder()
                    .counselSession(counselSession)
                    .counselRecord("의약 상담을 진행합니다. 아주 좋습니다. 뭐가 문제일까요?")
                    .counselRecordHighlights(List.of("아주", "뭐가"))
                    .build();

            medicationCounsel.setId(medicationCounselId);

            entityManager.persist(medicationCounsel);
        }
    }

    private void addMedicationRecordHist(String counselSessionId, List<String> medicationRecordHistIds) {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);
        String jpql = "SELECT m FROM Medication m";
        TypedQuery<Medication> query = entityManager.createQuery(jpql, Medication.class);
        query.setMaxResults(medicationRecordHistIds.size());
        List<Medication> medications = query.getResultList();
        int idx = 0;

        for (Medication medication : medications) {
            if (entityManager.find(MedicationRecordHist.class, medicationRecordHistIds.get(idx)) == null) {
                MedicationRecordHist medicationRecordHist = MedicationRecordHist.builder()
                        .counselSession(counselSession)
                        .medication(medication) // Associate with a Medication
                        .medicationDivision(MedicationDivision.PRESCRIPTION) // Example Enum
                        .name(medication.getItemName())
                        .usageObject("그냥")
                        .prescriptionDate(counselSession.getScheduledStartDateTime().minusDays(2).toLocalDate())
                        .prescriptionDays(7)
                        .unit("mg")
                        .usageStatus(MedicationUsageStatus.AS_NEEDED) // Example Enum
                        .build();

                medicationRecordHist.setId(medicationRecordHistIds.get(idx++));

                entityManager.persist(medicationRecordHist);
            }
        }

    }

    private void addWasteMedicationDisposal(String counselSessionId, String wasteMedicationDisposalId) {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);

        if (entityManager.find(WasteMedicationDisposal.class, wasteMedicationDisposalId) == null) {
            WasteMedicationDisposal wasteMedicationDisposal = WasteMedicationDisposal
                    .builder()
                    .counselSession(counselSession)
                    .unusedReasons(List.of("다른 약으로 대체함"))
                    .unusedReasonDetail("")
                    .drugRemainActionType(DrugRemainActionType.DOCTOR_OR_PHARMACIST)
                    .recoveryAgreementType(RecoveryAgreementType.AGREE)
                    .wasteMedicationGram(100)
                    .build();

            wasteMedicationDisposal.setId(wasteMedicationDisposalId);

            entityManager.persist(wasteMedicationDisposal);
        }
    }

    private void addWasteMedicationRecord(String counselSessionId, List<String> wasteMedicationRecordIds) {

        CounselSession counselSession = entityManager.getReference(CounselSession.class, counselSessionId);
        String jpql = "SELECT m FROM Medication m";
        TypedQuery<Medication> query = entityManager.createQuery(jpql, Medication.class);
        query.setMaxResults(wasteMedicationRecordIds.size());
        List<Medication> medications = query.getResultList();
        int idx = 0;

        for (Medication medication : medications) {

            if (entityManager.find(WasteMedicationRecord.class, wasteMedicationRecordIds.get(idx)) == null) {
                WasteMedicationRecord wasteMedicationRecord = WasteMedicationRecord.builder()
                        .counselSession(counselSession)
                        .medication(medication)
                        .medicationName(medication.getItemName())
                        .disposalReason("그냥")
                        .unit(100)
                        .build();

                wasteMedicationRecord.setId(wasteMedicationRecordIds.get(idx++));

                entityManager.persist(wasteMedicationRecord);
            }
        }

    }

    private LocalDate getRandomDate(String startDate, String endDate) {

        long start = LocalDate.parse(startDate).toEpochDay();
        long end = LocalDate.parse(endDate).toEpochDay();

        long randomDay = start + random.nextInt((int) (end - start));

        return LocalDate.ofEpochDay(randomDay);

    }

    private String getRandomPhoneNumber() {
        return "010" + String.format("%08d", random.nextInt(100000000));
    }

}
