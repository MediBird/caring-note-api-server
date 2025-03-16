package com.springboot.api.counselcard.entity;

import com.springboot.api.counselsession.entity.CounselSession;


interface CounselCardInitializer {

    void initializeCard(CounselCard card, CounselCard previousCard);
}

public class CounselCardFactory {

    public static CounselCard createFromSession(CounselSession counselSession,
        CounselCard previousCard) {
        CounselCard counselCard = new CounselCard();
        counselCard.initialize(counselSession);

        CounselCardInitializer initializer = getInitializer(counselSession);
        initializer.initializeCard(counselCard, previousCard);

        return counselCard;
    }

    private static CounselCardInitializer getInitializer(CounselSession counselSession) {
        boolean isDisabled = Boolean.TRUE.equals(counselSession.getCounselee().getIsDisability());
        return isDisabled ? new DisabledCounselCardInitializer()
            : new DefaultCounselCardInitializer();
    }
}

class DefaultCounselCardInitializer implements CounselCardInitializer {

    @Override
    public void initializeCard(CounselCard card, CounselCard previousCard) {
        if (previousCard != null) {
            card.copyValuesFrom(previousCard);
        } else {
            card.initializeDefaultValues();
        }
    }
}

class DisabledCounselCardInitializer implements CounselCardInitializer {

    @Override
    public void initializeCard(CounselCard card, CounselCard previousCard) {
        if (previousCard != null) {
            card.copyValuesFrom(previousCard);
            card.copyDisabledSpecificValueFrom(previousCard);
        } else {
            card.initializeDefaultValues();
            card.initializeDisabledSpecificDefaults();
        }
    }
}