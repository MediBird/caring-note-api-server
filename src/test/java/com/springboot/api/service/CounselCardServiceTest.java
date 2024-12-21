package com.springboot.api.service;


import com.springboot.api.dto.counselcard.SelectPreviousItemListByInformationNameAndInformationItemNameRes;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CounselCardServiceTest {
    private static final Logger log = LoggerFactory.getLogger(CounselCardServiceTest.class);

    @Autowired
    private CounselCardService counselCardService;


    @Test
    @Sql({
            "/sql/test-counselee.sql"
            ,"/sql/test-counselor.sql"
            ,"/sql/test-counsel-session.sql"
            ,"/sql/test-counsel-card.sql"
    })
    public void selectPreviousCounselCardItemListByCounselSessionId() {

        String counselorId = "TEST-COUNSELOR-01";
        String counselSessionId ="TEST-COUNSEL-SESSION-01";
        String informationName = "healthInformation";
        String informationItemName = "diseaseInfo";

        List<SelectPreviousItemListByInformationNameAndInformationItemNameRes> selectPreviousItemListResListByInformationNameAndInformationItemName
                = counselCardService.selectPreviousCounselCardItemListByCounselSessionId(
                        counselorId
                ,counselSessionId
                ,informationName
                ,informationItemName
        );

        log.debug(selectPreviousItemListResListByInformationNameAndInformationItemName.toString());
        assertEquals(1, selectPreviousItemListResListByInformationNameAndInformationItemName.size());


    }
}

