package com.springboot.api.dto.medication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchMedicationByKeywordRes {
    private String id;
    private String itemName;
    private String itemImage;
}