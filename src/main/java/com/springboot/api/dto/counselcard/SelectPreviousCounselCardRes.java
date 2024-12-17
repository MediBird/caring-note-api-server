package com.springboot.api.dto.counselcard;

import com.fasterxml.jackson.databind.JsonNode;

public record SelectPreviousCounselCardRes(JsonNode baseInformation
        , JsonNode healthInformation
        , JsonNode livingInformation
        , JsonNode independentLifeInformation){}
