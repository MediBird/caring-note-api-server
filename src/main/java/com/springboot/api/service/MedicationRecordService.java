package com.springboot.api.service;

import org.springframework.stereotype.Service;

import com.springboot.api.repository.MedicationRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationRecordService {

    private final MedicationRecordRepository medicationRecordRepository;

}
