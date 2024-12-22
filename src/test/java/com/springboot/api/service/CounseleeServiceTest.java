package com.springboot.api.service;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;

@SpringBootTest
@Transactional
public class CounseleeServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CounseleeServiceTest.class);
    @Autowired
    private CounseleeService counseleeService;


    @Test
    @Sql({
            "/sql/test-counselee.sql"
            ,"/sql/test-counselor.sql"
            ,"/sql/test-counsel-session.sql"
            ,"/sql/test-counsel-card.sql"
    })
    public void selectByCounselSessionId() {

        String counselorId = "TEST-COUNSELOR-01";
        String counselSessionId ="TEST-COUNSEL-SESSION-01";
        String counseleeId ="TEST-COUNSELEE-01";

        SelectCounseleeBaseInformationByCounseleeIdRes selectCounseleeBaseInformationByCounseleeIdRes =  counseleeService.selectCounseleeBaseInformationByCounseleeId(
                counselSessionId
                ,counseleeId
        );

        log.debug(selectCounseleeBaseInformationByCounseleeIdRes.toString());

    }


}
