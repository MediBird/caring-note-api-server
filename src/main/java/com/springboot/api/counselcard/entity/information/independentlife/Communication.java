package com.springboot.api.counselcard.entity.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.enums.CommunicationType;
import com.springboot.enums.HearingType;
import com.springboot.enums.SightType;
import com.springboot.enums.UsingKoreanType;
import jakarta.persistence.Column;
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

    public static Communication from(CommunicationDTO communicationDTO) {
        Communication communication = new Communication();
        communication.sights = Objects.requireNonNullElse(communicationDTO.sights(), Set.of());
        communication.hearings = Objects.requireNonNullElse(communicationDTO.hearings(), Set.of());
        communication.communications = Objects.requireNonNull(communicationDTO.communications(),
            "언어 소통 방법을 입력해주세요.");
        return communication;
    }

    public void update(CommunicationDTO communication) {
        this.sights = Objects.requireNonNullElse(communication.sights(), this.sights);
        this.hearings = Objects.requireNonNullElse(communication.hearings(), this.hearings);
        this.communications = Objects.requireNonNullElse(communication.communications(), this.communications);
    }
}
