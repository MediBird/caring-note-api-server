package com.springboot.api.service;

import com.springboot.api.dto.medicationcounsel.SelectPreviousByCounselSessionIdRes;
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
        String counselorId ="TEST-COUNSELOR-02";

        //then
        SelectPreviousByCounselSessionIdRes  selectPreviousByCounselSessionIdRes = medicationCounselService
                .selectPreviousByCounselSessionId(counselorId, counselSessionId);

        log.debug(selectPreviousByCounselSessionIdRes.toString());
        //valid
        assertEquals(selectPreviousByCounselSessionIdRes.previousCounselSessionId(),"TEST-COUNSEL-SESSION-00");
    }

}