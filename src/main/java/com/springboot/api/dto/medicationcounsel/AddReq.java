package com.springboot.api.dto.medicationcounsel;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.CounselNeedStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddReq {
    @NotBlank
    private String counselSessionId;

    private String counselRecord;

    private List<String> counselRecordHighlights;

    @ValidEnum(enumClass = CounselNeedStatus.class)
    private CounselNeedStatus counselNeedStatus;

}
