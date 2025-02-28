package com.springboot.api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.api.common.converter.ListStringConverter;
import com.springboot.enums.wasteMedication.DrugRemainActionType;
import com.springboot.enums.wasteMedication.RecoveryAgreementType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "waste_medication_disposals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession" })
@ToString(callSuper = true, exclude = { "counselSession" })
public class WasteMedicationDisposal extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    @JsonIgnore
    private CounselSession counselSession;

    @Convert(converter = ListStringConverter.class)
    @Column(name = "unused_reasons", columnDefinition = "TEXT", nullable = true)
    private List<String> unusedReasons;

    @Column(name = "unused_reason_detail")
    private String unusedReasonDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "drug_remain_action_type", nullable = true)
    private DrugRemainActionType drugRemainActionType;

    @Column(name = "drug_remain_action_detail")
    private String drugRemainActionDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_agreement_type", nullable = true)
    private RecoveryAgreementType recoveryAgreementType;

    @Column(name = "waste_medication_gram")
    private Integer wasteMedicationGram;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
