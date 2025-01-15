INSERT INTO counsel_cards (
    id,
    counsel_session_id,
    base_information,
    health_information,
    living_information,
    independent_life_information,
    card_record_status,
    created_datetime,
    updated_datetime
) VALUES (
             'TEST-COUNSEL-CARD-00', -- 고유 ID
             'TEST-COUNSEL-SESSION-00', -- CounselSession 외래 키
             '{
                 "version": 1.0,
                 "baseInfo": {
                     "counseleeId": "string",
                     "name": "김늘픔",
                     "birthDate": "1995-07-19",
                     "counselSessionOrder": "1회차",
                     "lastCounselDate": "2024-12-19",
                     "healthInsuranceType": "VETERANS_BENEFITS"
                 },
                 "counselPurposeAndNote": {
                     "counselPurpose": ["약물 부작용 상담", "약물 복용 관련 상담"],
                     "SignificantNote": "특이사항",
                     "MedicationNote": "복약 관련 메모"
                 }
             }',
             '{
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
             }',
             '{
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
             }',
             '{
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
             }',
             'RECORDED',
             CURRENT_TIMESTAMP(),
             CURRENT_TIMESTAMP()
         );

INSERT INTO counsel_cards (
    id,
    counsel_session_id,
    base_information,
    health_information,
    living_information,
    independent_life_information,
    card_record_status,
    created_datetime,
    updated_datetime
) VALUES (
             'TEST-COUNSEL-CARD-01', -- 고유 ID
             'TEST-COUNSEL-SESSION-01', -- CounselSession 외래 키
             '{
                 "version": 1.0,
                 "baseInfo": {
                     "counseleeId": "string",
                     "name": "김늘픔2",
                     "birthDate": "1995-07-18",
                     "counselSessionOrder": "1회차",
                     "lastCounselDate": "2024-12-20",
                     "healthInsuranceType": "MEDICAL_AID"
                 },
                 "counselPurposeAndNote": {
                     "counselPurpose": ["약물 부작용 상담", "약물 복용 관련 상담"],
                     "SignificantNote": "특이사항",
                     "MedicationNote": "복약 관련 메모"
                 }
             }',
             '{
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
             }',
             '{
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
             }',
             '{
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
             }',
             'RECORDED',
             CURRENT_TIMESTAMP(),
             CURRENT_TIMESTAMP()
         );
