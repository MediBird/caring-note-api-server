package com.springboot.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "medications")
@EqualsAndHashCode(callSuper = true, exclude = {"contraindications"})
@ToString(callSuper = true, exclude = {"contraindications"})
public class Medication extends BaseEntity{

        // 약 이름
    @Column(nullable = false, unique = true)
    @NotBlank(message = "약 이름은 필수 입력 항목입니다.")
    private String name;

    // 검색용 특성 정보
    @ElementCollection
    @CollectionTable(name = "medication_characteristics", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "characteristic")
    private Set<String> characteristics = new HashSet<>();

    // 폐기 시점 (개봉 후 며칠 뒤 폐기해야 하는지)
    @Min(value = 0, message = "폐기까지의 일수는 0 이상이어야 합니다.")
    private int daysUntilDiscard;

    // 약물 유형 (예: 알약, 캡슐, 액체 등)
    private String medicationType;

    // 성분
    @ElementCollection
    @CollectionTable(name = "medication_ingredients", joinColumns = @JoinColumn(name = "medication_id"))
    @Column(name = "ingredient")
    private Set<String> ingredients = new HashSet<>();

    // 용량 정보
    private String dosageStrength;

    // 효능/효과
    @Lob
    private String indications;

    // 부작용
    @Lob
    private String sideEffects;

    // 보관 방법
    private String storageInstructions;

    // 제조사
    private String manufacturer;

    // 복약 주의사항
    @Lob
    private String precautions;

    // 승인 번호
    private String approvalNumber;

    // 위험한 약물 상호 작용 (자기 자신과의 다대다 관계)
    @ManyToMany
    @JoinTable(
        name = "medication_contraindications",
        joinColumns = @JoinColumn(name = "medication_id"),
        inverseJoinColumns = @JoinColumn(name = "contraindicated_medication_id")
    )
    private Set<Medication> contraindications = new HashSet<>();

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
