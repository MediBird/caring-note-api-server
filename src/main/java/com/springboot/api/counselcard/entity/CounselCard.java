package com.springboot.api.counselcard.entity;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselcard.dto.AddCounselCardReq;
import com.springboot.api.counselcard.dto.UpdateCounselCardReq;
import com.springboot.api.counselcard.entity.information.base.CounselPurposeAndNote;
import com.springboot.api.counselcard.entity.information.health.Allergy;
import com.springboot.api.counselcard.entity.information.health.DiseaseInfo;
import com.springboot.api.counselcard.entity.information.health.MedicationSideEffect;
import com.springboot.api.counselcard.entity.information.living.Drinking;
import com.springboot.api.counselcard.entity.information.living.Exercise;
import com.springboot.api.counselcard.entity.information.living.MedicationManagement;
import com.springboot.api.counselcard.entity.information.living.Nutrition;
import com.springboot.api.counselcard.entity.information.living.Smoking;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.CardRecordStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "counsel_cards")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession" })
@ToString(callSuper = true, exclude = { "counselSession" })
public class CounselCard extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Embedded
    private CounselPurposeAndNote counselPurposeAndNote;

    @Embedded
    private Allergy allergy;

    @Embedded
    private DiseaseInfo diseaseInfo;

    @Embedded
    private MedicationSideEffect medicationSideEffect;

    @Embedded
    private Drinking drinking;

    @Embedded
    private Exercise exercise;

    @Embedded
    private MedicationManagement medicationManagement;

    @Embedded
    private Nutrition nutrition;

    @Embedded
    private Smoking smoking;

    @Column
    @Enumerated(EnumType.STRING)
    private CardRecordStatus cardRecordStatus;

    public static CounselCard of(CounselSession counselSession, AddCounselCardReq addCounselCardReq){
        CounselCard counselCard = new CounselCard();
        counselCard.counselSession = counselSession;
        counselCard.counselPurposeAndNote = CounselPurposeAndNote.from(addCounselCardReq.counselPurposeAndNote());
        counselCard.allergy = Allergy.from(addCounselCardReq.allergy());
        counselCard.diseaseInfo = DiseaseInfo.from(addCounselCardReq.diseaseInfo());
        counselCard.medicationSideEffect = MedicationSideEffect.from(addCounselCardReq.medicationSideEffect());
        counselCard.drinking = Drinking.from(addCounselCardReq.drinking());
        counselCard.exercise = Exercise.from(addCounselCardReq.exercise());
        counselCard.medicationManagement = MedicationManagement.from(addCounselCardReq.medicationManagement());
        counselCard.nutrition = Nutrition.from(addCounselCardReq.nutrition());
        counselCard.smoking = Smoking.from(addCounselCardReq.smoking());
        counselCard.cardRecordStatus = addCounselCardReq.cardRecordStatus();
        return counselCard;
    }

    public void update(UpdateCounselCardReq updateCounselCardReq){
        this.counselPurposeAndNote.update(updateCounselCardReq.counselPurposeAndNote());
        this.allergy.update(updateCounselCardReq.allergy());
        this.diseaseInfo.update(updateCounselCardReq.diseaseInfo());
        this.medicationSideEffect.update(updateCounselCardReq.medicationSideEffect());
        this.drinking.update(updateCounselCardReq.drinking());
        this.exercise.update(updateCounselCardReq.exercise());
        this.medicationManagement.update(updateCounselCardReq.medicationManagement());
        this.nutrition.update(updateCounselCardReq.nutrition());
        this.smoking.update(updateCounselCardReq.smoking());
        this.cardRecordStatus = Objects.requireNonNullElse(updateCounselCardReq.cardRecordStatus(), this.cardRecordStatus);
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
