package com.springboot.api.common.config.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.springboot.api.domain.CounselCard;
import com.springboot.api.repository.CounselCardRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CounselCardInitializer implements CommandLineRunner {

    private final CounselCardRepository counselCardRepository;

    @Override
    public void run(String... args) {
        CounselCard counselCard = new CounselCard();
        counselCard.setCounselor(counselor);
        counselCard.setCounselee(counselee);
        counselCard.setCounselSession(counselSession);
        counselCardRepository.save(counselCard);
    }
}
