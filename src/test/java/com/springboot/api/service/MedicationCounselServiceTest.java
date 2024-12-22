package com.springboot.api.service;

import com.springboot.api.dto.medicationcounsel.SelectPreviousMedicationCounselRes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
class MedicationCounselServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MedicationCounselServiceTest.class);
    @Autowired
    MedicationCounselService medicationCounselService;

    @Test
    @Sql({
            "/sql/test-counselee.sql"
            ,"/sql/test-counselor.sql"
            ,"/sql/test-counsel-session.sql"
            ,"/sql/test-medication-counsel.sql"
    })
    void selectPreviousByCounselSessionId() {

        //when
        String counselSessionId ="TEST-COUNSEL-SESSION-01";

        //then
        SelectPreviousMedicationCounselRes selectPreviousMedicationCounselRes = medicationCounselService
                .selectPreviousMedicationCounsel(counselSessionId);

        log.debug(selectPreviousMedicationCounselRes.toString());
        //valid
        assertEquals(selectPreviousMedicationCounselRes.previousCounselSessionId(),"TEST-COUNSEL-SESSION-00");
    }

}