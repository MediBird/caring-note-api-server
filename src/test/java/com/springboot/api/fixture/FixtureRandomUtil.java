package com.springboot.api.fixture;

import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;
import java.time.LocalDate;
import java.util.Random;

public class FixtureRandomUtil {

    private static final Random RANDOM = new Random();

    private static final String[] FAMILY_NAMES = {
        "김", "이", "박", "최", "정", "조", "한", "장", "임", "오"
    };

    private static final String[] FIRST_NAMES = {
        "춘자", "영자", "순이", "말자",
        "복순", "영수", "철수", "만수",
        "갑돌", "갑순"
    };

    private static final String[] WELFARE_INSTITUTIONS = {
        "동작노인복지관", "서울중앙경로당", "부산사랑의집", "서울시청",
        "부산복지센터", "인천복지회관", "대구상담소"
    };

    private static final String[] ADDRESSES = {
        "서울시 강북구", "서울시 종로구", "부산시 해운대구", "대전시 유성구",
        "부산시 동래구", "대구시 중구"
    };

    private static final String[] CARE_MANAGER_NAMES = {
        "김수정", "이민아", "박정현", "최유리", "정수빈",
        "한예진", "오세영", "강지윤", "장미경", "윤지현",
        "조은영", "임다혜", "송지연", "황지수", "서지우",
        "안소연", "권미경", "신혜선", "전하은", "홍가은"
    };

    public static String generateUUID() {
        StringBuilder sb = new StringBuilder(26);
        char[] BASE32_CHARS = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
        for (int i = 0; i < 26; i++) {
            sb.append(BASE32_CHARS[RANDOM.nextInt(BASE32_CHARS.length)]);
        }
        return sb.toString();
    }
    public static String generateElderlyKoreanName() {
        String family = FAMILY_NAMES[RANDOM.nextInt(FAMILY_NAMES.length)];
        String first = FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
        return family + first;
    }

    public static LocalDate generateElderlyBirthDate() {
        int year = 1920 + RANDOM.nextInt(51); // 1920~1970
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(28); // 안전하게 28일까지
        return LocalDate.of(year, month, day);
    }

    public static String generatePhoneNumber() {
        int mid = 1000 + RANDOM.nextInt(9000);
        int last = 1000 + RANDOM.nextInt(9000);
        return String.format("010-%04d-%04d", mid, last);
    }

    public static int generateCounselCount() {
        return 1 + RANDOM.nextInt(50); // 1 ~ 200회
    }

    public static LocalDate generateRegistrationDate() {
        return LocalDate.now().minusDays(RANDOM.nextInt(365 * 5));
    }

    public static LocalDate generateLastCounselDate() {
        return LocalDate.now().minusDays(RANDOM.nextInt(365));
    }


    public static GenderType generateGender() {
        return RANDOM.nextBoolean() ? GenderType.MALE : GenderType.FEMALE;
    }

    public static HealthInsuranceType generateInsuranceType() {
        HealthInsuranceType[] values = HealthInsuranceType.values();
        return values[RANDOM.nextInt(values.length)];
    }

    public static String generateWelfareInstitution() {
        return WELFARE_INSTITUTIONS[RANDOM.nextInt(WELFARE_INSTITUTIONS.length)];
    }

    public static String generateAddress() {
        return ADDRESSES[RANDOM.nextInt(ADDRESSES.length)];
    }

    public static String generateCareManagerName() {
        return CARE_MANAGER_NAMES[RANDOM.nextInt(CARE_MANAGER_NAMES.length)];
    }

    public static Boolean generateDisabilityStatus() {
        return RANDOM.nextBoolean();
    }

    public static String generateNote() {
        return "정기 상담 대상자";
    }
}
