package com.springboot.api.counselsession.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.api.common.converter.ListStringConverter;
import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.enums.wasteMedication.DrugRemainActionType;
import com.springboot.api.counselsession.enums.wasteMedication.RecoveryAgreementType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "waste_medication_disposals")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession" })
@ToString(callSuper = true, exclude = { "counselSession" })
public class WasteMedicationDisposal extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    @JsonIgnore
    private CounselSession counselSession;

    @Convert(converter = ListStringConverter.class)
    @Column(name = "unused_reasons", columnDefinition = "TEXT")
    private List<String> unusedReasons;

    @Column(name = "unused_reason_detail")
    private String unusedReasonDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "drug_remain_action_type")
    private DrugRemainActionType drugRemainActionType;

    @Column(name = "drug_remain_action_detail")
    private String drugRemainActionDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "recovery_agreement_type")
    private RecoveryAgreementType recoveryAgreementType;

    @Column(name = "waste_medication_gram")
    private Integer wasteMedicationGram;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
