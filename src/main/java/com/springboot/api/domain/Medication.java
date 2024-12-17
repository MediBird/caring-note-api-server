package com.springboot.api.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "medications")
@EqualsAndHashCode(callSuper = true, exclude = {"contraindications"})
@ToString(callSuper = true, exclude = {"contraindications"})
public class Medication extends BaseEntity{

        // 약 이름
    @Column(nullable = false, unique = true)
    @NotBlank(message = "약 이름은 필수 입력 항목입니다.")
    private String itemName;

    private Integer itemSeq;
  
    private Integer entpSeq;
    private String entpName;
    private String itemImage;
    private String chart;
    private String printFront;
    private String printBack;
    private String drugShape;
    private String colorClass1;
    private String colorClass2;
    private String lineFront;
    private String lineBack;
    private float lengLong;
    private float lengShort;
    private float thick;
    private Date imgRegistTs;
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
    private String ediCode;

    // 폐기 시점 (개봉 후 며칠 뒤 폐기해야 하는지)
    @Min(value = 0, message = "폐기까지의 일수는 0 이상이어야 합니다.")
    private int daysUntilDiscard;


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
