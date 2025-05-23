package com.springboot.api.counselcard.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselcard.dto.request.UpdateBaseInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateCounselCardReq;
import com.springboot.api.counselcard.dto.request.UpdateHealthInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateIndependentLifeInformationReq;
import com.springboot.api.counselcard.dto.request.UpdateLivingInformationReq;
import com.springboot.api.counselcard.entity.information.base.CounselPurposeAndNote;
import com.springboot.api.counselcard.entity.information.health.Allergy;
import com.springboot.api.counselcard.entity.information.health.DiseaseInfo;
import com.springboot.api.counselcard.entity.information.health.MedicationSideEffect;
import com.springboot.api.counselcard.entity.information.independentlife.Communication;
import com.springboot.api.counselcard.entity.information.independentlife.Evacuation;
import com.springboot.api.counselcard.entity.information.independentlife.Walking;
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
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "counsel_cards")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class CounselCard extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    @SuppressWarnings("FieldMayBeFinal")
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

    @Embedded
    private Communication communication;

    @Embedded
    private Evacuation evacuation;

    @Embedded
    private Walking walking;

    @Column
    @Enumerated(EnumType.STRING)
    private CardRecordStatus cardRecordStatus;

    public static CounselCard createFromSession(CounselSession counselSession) {
        CounselCard counselCard = CounselCard.builder()
            .counselSession(counselSession)
            .cardRecordStatus(CardRecordStatus.NOT_STARTED)
            .build();
        counselCard.initializeDefaultValues();
        return counselCard;
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @PostLoad
    public void onPostLoad() {
        this.counselPurposeAndNote =
            this.counselPurposeAndNote == null ? CounselPurposeAndNote.initializeDefault() : this.counselPurposeAndNote;
        this.allergy = this.allergy == null ? Allergy.initializeDefault() : this.allergy;
        this.diseaseInfo = this.diseaseInfo == null ? DiseaseInfo.initializeDefault() : this.diseaseInfo;
        this.medicationSideEffect =
            this.medicationSideEffect == null ? MedicationSideEffect.initializeDefault() : this.medicationSideEffect;
        this.drinking = this.drinking == null ? Drinking.initializeDefault() : this.drinking;
        this.exercise = this.exercise == null ? Exercise.initializeDefault() : this.exercise;
        this.medicationManagement =
            this.medicationManagement == null ? MedicationManagement.initializeDefault() : this.medicationManagement;
        this.nutrition = this.nutrition == null ? Nutrition.initializeDefault() : this.nutrition;
        this.smoking = this.smoking == null ? Smoking.initializeDefault() : this.smoking;
        this.communication = this.communication == null ? Communication.initializeDefault() : this.communication;
        this.evacuation = this.evacuation == null ? Evacuation.initializeDefault() : this.evacuation;
        this.walking = this.walking == null ? Walking.initializeDefault() : this.walking;
    }

    public void importPreviousCardData(CounselCard previousCard) {
        if (previousCard == null) {
            return;
        }
        copyValuesFrom(previousCard);
    }

    public void updateStatusToInProgress() {
        if (this.cardRecordStatus.equals(CardRecordStatus.COMPLETED)) {
            throw new IllegalArgumentException("이미 완료된 상담 카드입니다");
        }
        this.cardRecordStatus = CardRecordStatus.IN_PROGRESS;
    }

    public void updateStatusToCompleted() {
        if (!this.cardRecordStatus.equals(CardRecordStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("상담 카드가 진행 중이 아닙니다");
        }
        this.cardRecordStatus = CardRecordStatus.COMPLETED;
    }

    void copyValuesFrom(CounselCard previousCard) {
        this.counselPurposeAndNote = CounselPurposeAndNote.copy(previousCard.getCounselPurposeAndNote());
        this.allergy = Allergy.copy(previousCard.getAllergy());
        this.diseaseInfo = DiseaseInfo.copy(previousCard.getDiseaseInfo());
        this.medicationSideEffect = MedicationSideEffect.copy(previousCard.getMedicationSideEffect());
        this.drinking = Drinking.copy(previousCard.getDrinking());
        this.exercise = Exercise.copy(previousCard.getExercise());
        this.medicationManagement = MedicationManagement.copy(previousCard.getMedicationManagement());
        this.nutrition = Nutrition.copy(previousCard.getNutrition());
        this.smoking = Smoking.copy(previousCard.getSmoking());
        this.communication = Communication.copy(previousCard.getCommunication());
        this.evacuation = Evacuation.copy(previousCard.getEvacuation());
        this.walking = Walking.copy(previousCard.getWalking());
    }

    void initializeDefaultValues() {
        this.counselPurposeAndNote = CounselPurposeAndNote.initializeDefault();
        this.allergy = Allergy.initializeDefault();
        this.diseaseInfo = DiseaseInfo.initializeDefault();
        this.medicationSideEffect = MedicationSideEffect.initializeDefault();
        this.drinking = Drinking.initializeDefault();
        this.exercise = Exercise.initializeDefault();
        this.medicationManagement = MedicationManagement.initializeDefault();
        this.nutrition = Nutrition.initializeDefault();
        this.smoking = Smoking.initializeDefault();
        this.communication = Communication.initializeDefault();
        this.evacuation = Evacuation.initializeDefault();
        this.walking = Walking.initializeDefault();
    }

    public void update(UpdateCounselCardReq updateCounselCardReq) {
        this.counselPurposeAndNote.update(updateCounselCardReq.counselPurposeAndNote());
        this.allergy.update(updateCounselCardReq.allergy());
        this.diseaseInfo.update(updateCounselCardReq.diseaseInfo());
        this.medicationSideEffect.update(updateCounselCardReq.medicationSideEffect());
        this.drinking.update(updateCounselCardReq.drinking());
        this.exercise.update(updateCounselCardReq.exercise());
        this.medicationManagement.update(updateCounselCardReq.medicationManagement());
        this.nutrition.update(updateCounselCardReq.nutrition());
        this.smoking.update(updateCounselCardReq.smoking());
        this.communication.update(updateCounselCardReq.communication());
        this.evacuation.update(updateCounselCardReq.evacuation());
        this.walking.update(updateCounselCardReq.walking());
    }
}
