package com.springboot.api.counselcard.entity.information.independentlife;

import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.enums.EvacuationType;
import jakarta.persistence.Embeddable;
import java.util.HashSet;
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

    public static Evacuation initializeDefault() {
        Evacuation evacuation = new Evacuation();
        evacuation.evacuations = Set.of();
        evacuation.evacuationNote = "";
        return evacuation;
    }

    public static Evacuation copy(Evacuation evacuation) {
        Evacuation copiedEvacuation = new Evacuation();
        copiedEvacuation.evacuations = new HashSet<>(evacuation.evacuations);
        copiedEvacuation.evacuationNote = evacuation.evacuationNote;
        return copiedEvacuation;
    }

    public void update(EvacuationDTO evacuation) {
        this.evacuations = Objects.requireNonNullElse(evacuation.evacuations(), this.evacuations);
        this.evacuationNote = Objects.requireNonNullElse(evacuation.evacuationNote(), this.evacuationNote);
    }
}
