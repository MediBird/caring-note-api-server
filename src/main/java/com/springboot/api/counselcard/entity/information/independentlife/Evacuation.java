package com.springboot.api.counselcard.entity.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.enums.EvacuationType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Evacuation {
    Set<EvacuationType> evacuations;

    String evacuationNote;

    public static Evacuation from(EvacuationDTO evacuationDTO) {
        Evacuation evacuation = new Evacuation();
        evacuation.evacuations = Objects.requireNonNullElse(evacuationDTO.evacuations(), Set.of());
        evacuation.evacuationNote = Objects.requireNonNullElse(evacuationDTO.evacuationNote(), "");
        return evacuation;
    }

    public void update(EvacuationDTO evacuation) {
        this.evacuations = Objects.requireNonNullElse(evacuation.evacuations(), this.evacuations);
        this.evacuationNote = Objects.requireNonNullElse(evacuation.evacuationNote(), this.evacuationNote);
    }
}
