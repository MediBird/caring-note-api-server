package com.springboot.api.medication.entity;

import com.springboot.api.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@Table(name = "medications", indexes = {
    @Index(name = "idx_item_name", columnList = "item_name"),
    @Index(name = "idx_item_name_chosung", columnList = "item_name_chosung")
})
@EqualsAndHashCode(callSuper = true, exclude = {"contraindications"})
@ToString(callSuper = true, exclude = {"contraindications"})
public class Medication extends BaseEntity {

    // 약 이름
    @Column(nullable = false, length = 255)
    @NotBlank(message = "약 이름은 필수 입력 항목입니다.")
    private String itemName;
    @Column(nullable = false, length = 255, name = "item_name_chosung")
    private String itemNameChosung;
    @Column(nullable = false)
    private Integer itemSeq;

    @Column(length = 255)
    private String entpName;
    @Column(length = 3000)
    private String itemImage;
    @Column(length = 3000)
    private String chart;
    @Column(length = 255)
    private String printFront;
    @Column(length = 255)
    private String printBack;
    @Column(length = 255)
    private String drugShape;
    @Column(length = 255)
    private String colorClass1;
    @Column(length = 255)
    private String colorClass2;
    private String lineFront;
    private String lineBack;
    private Float lengLong;
    private Float lengShort;
    private Float thick;
    @Temporal(TemporalType.DATE)
    private LocalDate imgRegistTs;
    private Integer classNo;
    private String className;
    private String etcOtcName;
    private String formCodeName;
    private String markCodeFrontAnal;
    private String markCodeBackAnal;
    private String markCodeFront;
    private String markCodeBack;
    private String markCodeFrontImg;
    private String markCodeBackImg;
    private String itemEngName;
    @Temporal(TemporalType.DATE)
    private LocalDate itemPermitDate;
    private String ediCode;

    // 폐기 시점 (개봉 후 며칠 뒤 폐기해야 하는지)
    @Min(value = 0, message = "폐기까지의 일수는 0 이상이어야 합니다.")
    private Integer daysUntilDiscard;

    // 위험한 약물 상호 작용 (자기 자신과의 다대다 관계)
    @ManyToMany
    @JoinTable(name = "medication_contraindications", joinColumns = @JoinColumn(name = "medication_id"), inverseJoinColumns = @JoinColumn(name = "contraindicated_medication_id"))
    @Builder.Default
    private Set<Medication> contraindications = new HashSet<>();

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
