package com.springboot.api.common.config.initializer;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Medication;
import com.springboot.api.domain.MedicationRecordHist;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationRecordHistRepository;
import com.springboot.api.repository.MedicationRepository;
import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicationRecordHistInitializer implements CommandLineRunner {

    private final MedicationRecordHistRepository medicationRecordHistRepository;

    private final CounselSessionRepository counselSessionRepository;

    private final MedicationRepository medicationRepository;

    @Override
    public void run(String... args) {
        String counselSessionId = "TEST-COUNSEL-SESSION-01";
        // Ensure CounselSession exists
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseGet(() -> counselSessionRepository.save(
                CounselSession.builder()
                        .build()
        ));
        // Fetch a Medication from the database= 
        Optional<Medication> optionalMedication = medicationRepository.findAll().stream().findFirst();
        MedicationRecordHist record;
        if (optionalMedication.isPresent()) {
            Medication medication = optionalMedication.get();

            // Create 10 examples
            for (int i = 1; i <= 2; i++) {
                record = MedicationRecordHist.builder()
                        .counselSession(counselSession)
                        .medication(medication) // Associate with a Medication
                        .medicationDivision(MedicationDivision.PRESCRIPTION) // Example Enum
                        .name("Medication " + i)
                        .usageObject("Usage Object " + i)
                        .prescriptionDate(LocalDate.now().minusDays(i))
                        .prescriptionDays(i)
                        .unit("mg")
                        .medicationUsageStatus(MedicationUsageStatus.AS_NEEDED) // Example Enum
                        .build();

                medicationRecordHistRepository.save(record);
            }
            for (int i = 1; i <= 2; i++) {
                record = new MedicationRecordHist();
                record.setCounselSession(counselSession);
                record.setMedication(medication); // Associate with a Medication
                record.setMedicationDivision(MedicationDivision.PRESCRIPTION); // Example Enum
                record.setName("Medication " + i);
                record.setUsageObject("Usage Object " + i);
                record.setPrescriptionDate(LocalDate.now().minusDays(i));
                record.setPrescriptionDays(i);
                record.setUnit("mg");
                record.setUsageStatus(MedicationUsageStatus.REGULAR); // Example Enum

                medicationRecordHistRepository.save(record);
            }
            for (int i = 1; i <= 2; i++) {
                record = new MedicationRecordHist();
                record.setCounselSession(counselSession);
                record.setMedication(medication); // Associate with a Medication
                record.setMedicationDivision(MedicationDivision.PRESCRIPTION); // Example Enum
                record.setName("Medication " + i);
                record.setUsageObject("Usage Object " + i);
                record.setPrescriptionDate(LocalDate.now().minusDays(i));
                record.setPrescriptionDays(i);
                record.setUnit("mg");
                record.setUsageStatus(MedicationUsageStatus.STOPPED);
                medicationRecordHistRepository.save(record);
            }

            for (int i = 1; i <= 2; i++) {
                record = new MedicationRecordHist();
                record.setCounselSession(counselSession);
                record.setMedication(medication); // Associate with a Medication
                record.setMedicationDivision(MedicationDivision.OTC); // Example Enum
                record.setName("Medication " + i);
                record.setUsageObject("Usage Object " + i);
                record.setUnit("mg");
                record.setUsageStatus(MedicationUsageStatus.REGULAR); // Example Enum
                medicationRecordHistRepository.save(record);
            }
            for (int i = 1; i <= 2; i++) {
                record = new MedicationRecordHist();
                record.setCounselSession(counselSession);
                record.setMedication(medication); // Associate with a Medication
                record.setMedicationDivision(MedicationDivision.OTC); // Example Enum
                record.setName("Medication " + i);
                record.setUsageObject("Usage Object " + i);
                record.setUnit("mg");
                record.setUsageStatus(MedicationUsageStatus.STOPPED); // Example Enum
                medicationRecordHistRepository.save(record);
            }

            System.out.println("No medication found in the database to associate with MedicationRecordHist.");
        }
    }
}
