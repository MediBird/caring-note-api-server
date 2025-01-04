package com.springboot.api.domain;

import java.util.List;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.enums.wasteMedication.DrugRemainActionType;
import com.springboot.enums.wasteMedication.RecoveryAgreementType;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "waste_medication_disposals")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class WasteMedicationDisposal extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    @JsonIgnore
    private CounselSession counselSession;

    @Type(ListArrayType.class)
    @Column(name = "unused_reasons", columnDefinition = "character varying[]", nullable = true)
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
