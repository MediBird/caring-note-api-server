package com.springboot.api.counselcard.entity.information.living;

import com.springboot.enums.MedicationAssistant;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationAssistantDetail {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationAssistant assistantType;

    @Column(length = 500) // 기타 설명을 저장할 필드
    private String customDescription;

}
