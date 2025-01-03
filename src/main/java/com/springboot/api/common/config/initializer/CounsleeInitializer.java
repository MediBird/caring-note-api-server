package com.springboot.api.common.config.initializer;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.springboot.api.domain.Counselee;
import com.springboot.api.repository.CounseleeRepository;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CounsleeInitializer implements CommandLineRunner {

    private final CounseleeRepository counseleeRepository;

    @Override
    public void run(String... args) {
        // TODO Auto-generated method stub
        Counselee counselee = new Counselee();
        counselee.setPhoneNumber("010-1234-5678");
        counselee.setName("홍길동");
        counselee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        counselee.setGenderType(GenderType.MALE);
        counselee.setHealthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE);
        counselee.setAddress("서울시 강남구 역삼동");
        counselee.setDisability(false);
        counselee.setRegistrationDate(LocalDate.now());
        counseleeRepository.save(counselee);
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
