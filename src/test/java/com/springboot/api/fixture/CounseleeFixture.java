package com.springboot.api.fixture;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.springboot.api.counselee.entity.Counselee;
import java.util.List;
import java.util.stream.IntStream;

public class CounseleeFixture {

    public Counselee createCounselee(){
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build();

        return fixtureMonkey.giveMeBuilder(Counselee.class)
                .set("id", FixtureRandomUtil.generateUUID())
                .set("createdBy", null)
                .set("updatedBy", null)
                .set("name", FixtureRandomUtil.generateElderlyKoreanName())
                .set("dateOfBirth", FixtureRandomUtil.generateElderlyBirthDate())
                .set("phoneNumber", FixtureRandomUtil.generatePhoneNumber())
                .set("counselCount", FixtureRandomUtil.generateCounselCount())
                .set("registrationDate", FixtureRandomUtil.generateRegistrationDate())
                .set("lastCounselDate", FixtureRandomUtil.generateLastCounselDate())
                .set("affiliatedWelfareInstitution", FixtureRandomUtil.generateWelfareInstitution())
                .set("note", FixtureRandomUtil.generateNote())
                .set("genderType", FixtureRandomUtil.generateGender())
                .set("healthInsuranceType", FixtureRandomUtil.generateInsuranceType())
                .set("address", FixtureRandomUtil.generateAddress())
                .set("isDisability", FixtureRandomUtil.generateDisabilityStatus())
                .set("careManagerName", FixtureRandomUtil.generateCareManagerName())
                .sample();
    }

    public Counselee createCounselee(String name){
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build();

        return fixtureMonkey.giveMeBuilder(Counselee.class)
            .set("id", FixtureRandomUtil.generateUUID())
            .set("createdBy", null)
            .set("updatedBy", null)
            .set("name", name)
            .set("dateOfBirth", FixtureRandomUtil.generateElderlyBirthDate())
            .set("phoneNumber", FixtureRandomUtil.generatePhoneNumber())
            .set("counselCount", FixtureRandomUtil.generateCounselCount())
            .set("registrationDate", FixtureRandomUtil.generateRegistrationDate())
            .set("lastCounselDate", FixtureRandomUtil.generateLastCounselDate())
            .set("affiliatedWelfareInstitution", FixtureRandomUtil.generateWelfareInstitution())
            .set("note", FixtureRandomUtil.generateNote())
            .set("genderType", FixtureRandomUtil.generateGender())
            .set("healthInsuranceType", FixtureRandomUtil.generateInsuranceType())
            .set("address", FixtureRandomUtil.generateAddress())
            .set("isDisability", FixtureRandomUtil.generateDisabilityStatus())
            .set("careManagerName", FixtureRandomUtil.generateCareManagerName())
            .sample();
    }

    public List<Counselee> createCounselees(int size) {
        FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build();

        return IntStream.range(0, size)
            .mapToObj(i -> fixtureMonkey.giveMeBuilder(Counselee.class)
                .set("id", FixtureRandomUtil.generateUUID())
                .set("createdBy", null)
                .set("updatedBy", null)
                .set("name", FixtureRandomUtil.generateElderlyKoreanName())
                .set("dateOfBirth", FixtureRandomUtil.generateElderlyBirthDate())
                .set("phoneNumber", FixtureRandomUtil.generatePhoneNumber())
                .set("counselCount", FixtureRandomUtil.generateCounselCount())
                .set("registrationDate", FixtureRandomUtil.generateRegistrationDate())
                .set("lastCounselDate", FixtureRandomUtil.generateLastCounselDate())
                .set("affiliatedWelfareInstitution", FixtureRandomUtil.generateWelfareInstitution())
                .set("note", FixtureRandomUtil.generateNote())
                .set("genderType", FixtureRandomUtil.generateGender())
                .set("healthInsuranceType", FixtureRandomUtil.generateInsuranceType())
                .set("address", FixtureRandomUtil.generateAddress())
                .set("isDisability", FixtureRandomUtil.generateDisabilityStatus())
                .set("careManagerName", FixtureRandomUtil.generateCareManagerName())
                .sample())
            .toList();
    }
}
