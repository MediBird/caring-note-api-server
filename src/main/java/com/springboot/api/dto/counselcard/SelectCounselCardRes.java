package com.springboot.api.dto.counselcard;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.enums.CardRecordStatus;

public record SelectCounselCardRes(
        String counselCardId
        , JsonNode baseInformation
        , JsonNode healthInformation
        , JsonNode livingInformation
        , JsonNode independentLifeInformation
        , CardRecordStatus cardRecordStatus){}
