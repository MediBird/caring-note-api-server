package com.springboot.api.dto.counselcard;

import java.util.Map;

public record SelectPreviousCounselCardRes(Map<String, Object> baseInformation
        ,Map<String, Object> healthInformation
        ,Map<String, Object> livingInformation
        ,Map<String, Object> selfReliantLivingInformation){}
