package com.springboot.api.counselcard.entity.information.independentlife;

import jakarta.persistence.ForeignKey;
import java.util.List;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.enums.CommunicationType;
import com.springboot.enums.HearingType;
import com.springboot.enums.SightType;
import com.springboot.enums.UsingKoreanType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Communication {

    @ElementCollection
    @CollectionTable(name = "communication_sights",
        joinColumns = @JoinColumn(name = "communication_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (communication_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<SightType> sights;

    @ElementCollection
    @CollectionTable(name = "communication_hearings",
        joinColumns = @JoinColumn(name = "communication_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (communication_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<HearingType> hearings;

    @Enumerated(EnumType.STRING)
    private CommunicationType communications;

    @ElementCollection
    @CollectionTable(name = "communication_using_koreans",
        joinColumns = @JoinColumn(name = "communication_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (communication_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<UsingKoreanType> usingKoreans;

    public static Communication initializeDefault() {
        Communication communication = new Communication();
        communication.sights = List.of();
        communication.hearings = List.of();
        communication.communications = null;
        communication.usingKoreans = List.of();
        return communication;
    }

    public static Communication copy(Communication communication) {
        Communication copiedCommunication = new Communication();
        copiedCommunication.sights = List.copyOf(communication.sights);
        copiedCommunication.hearings = List.copyOf(communication.hearings);
        copiedCommunication.communications = communication.communications;
        copiedCommunication.usingKoreans = List.copyOf(communication.usingKoreans);
        return copiedCommunication;
    }

    public void update(CommunicationDTO communication) {
        if (communication == null) {
            return;
        }

        this.sights = communication.sights();
        this.hearings = communication.hearings();
        this.communications = communication.communications();
        this.usingKoreans = communication.usingKoreans();
    }
}
