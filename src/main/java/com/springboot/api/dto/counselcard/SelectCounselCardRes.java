package com.springboot.api.dto.counselcard;

import com.springboot.enums.CardRecordStatus;

import java.util.Map;

public record SelectCounselCardRes(
        String counselCardId
        , Map<String, Object> baseInformation
        , Map<String, Object> healthInformation
        , Map<String, Object> livingInformation
        , Map<String, Object> independentLifeInformation
        , CardRecordStatus cardRecordStatus){}
