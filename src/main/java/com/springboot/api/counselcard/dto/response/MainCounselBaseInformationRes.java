package com.springboot.api.counselcard.dto.response;

import com.springboot.enums.CounselPurposeType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MainCounselBaseInformationRes {

    MainCounselRecord<List<CounselPurposeType>> counselPurpose;
    MainCounselRecord<String> significantNote;
    MainCounselRecord<String> medicationNote;
}


