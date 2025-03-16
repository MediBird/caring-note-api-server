package com.springboot.api.counselcard.entity.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.enums.CommunicationType;
import com.springboot.enums.HearingType;
import com.springboot.enums.SightType;
import com.springboot.enums.UsingKoreanType;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Communication {

    private Set<SightType> sights;

    private Set<HearingType> hearings;

    private CommunicationType communications;

    private Set<UsingKoreanType> usingKoreans;

    public static Communication initializeDefault() {
        Communication communication = new Communication();
        communication.sights = Set.of();
        communication.hearings = Set.of();
        communication.communications = null;
        communication.usingKoreans = Set.of();
        return communication;
    }

    public static Communication copy(Communication communication) {
        Communication copiedCommunication = new Communication();
        copiedCommunication.sights = Set.copyOf(communication.sights);
        copiedCommunication.hearings = Set.copyOf(communication.hearings);
        copiedCommunication.communications = communication.communications;
        copiedCommunication.usingKoreans = Set.copyOf(communication.usingKoreans);
        return copiedCommunication;
    }

    public void update(CommunicationDTO communication) {
        this.sights = Objects.requireNonNullElse(communication.sights(), this.sights);
        this.hearings = Objects.requireNonNullElse(communication.hearings(), this.hearings);
        this.communications = Objects.requireNonNullElse(communication.communications(), this.communications);
    }
}
