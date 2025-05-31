package com.springboot.api.counselcard.dto.response;

import com.springboot.enums.CounselPurposeType;
import java.util.List;

public record MainCounselBaseInformationRes(
    MainCounselRecord<List<CounselPurposeType>> counselPurpose,
    MainCounselRecord<String> significantNote,
    MainCounselRecord<String> medicationNote
) {

}


