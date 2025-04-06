package com.springboot.api.fixture;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.MonkeyStringArbitrary;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.springboot.api.counselee.entity.Counselee;
import java.util.List;
import java.util.stream.IntStream;

public class CounseleeFixture {

    private static final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .register(Counselee.class, builder -> builder.giveMeBuilder(Counselee.class)
            .set("id", FixtureRandomUtil.generateULID())
            .set("createdBy", null)
            .set("updatedBy", null)
            .set("name", FixtureRandomUtil.generateElderlyKoreanName())
            .set("dateOfBirth", FixtureRandomUtil.generateElderlyBirthDate())
            .set("phoneNumber", FixtureRandomUtil.generatePhoneNumber())
            .set("counselCount", FixtureRandomUtil.generateCounselCount())
            .set("registrationDate", FixtureRandomUtil.generateRegistrationDate())
            .set("lastCounselDate", FixtureRandomUtil.generateLastCounselDate())
            .set("affiliatedWelfareInstitution", FixtureRandomUtil.generateWelfareInstitution())
            .set("note", new MonkeyStringArbitrary().korean().ofLength(300))
            .set("genderType", FixtureRandomUtil.generateGender())
            .set("healthInsuranceType", FixtureRandomUtil.generateInsuranceType())
            .set("address", FixtureRandomUtil.generateAddress())
            .set("isDisability", FixtureRandomUtil.generateDisabilityStatus())
            .set("careManagerName", FixtureRandomUtil.generateCareManagerName())
        )
        .build();

    public static Counselee create() {
        return fixtureMonkey.giveMeOne(Counselee.class);
    }

    public static Counselee create(String name) {
        return fixtureMonkey.giveMeBuilder(Counselee.class)
            .set("name", name)
            .sample();
    }
    public static List<Counselee> createList(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> fixtureMonkey.giveMeOne(Counselee.class))
            .toList();
    }
}
