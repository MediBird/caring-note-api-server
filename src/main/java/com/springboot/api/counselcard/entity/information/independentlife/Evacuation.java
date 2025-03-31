package com.springboot.api.counselcard.entity.information.independentlife;

import jakarta.persistence.ForeignKey;
import java.util.List;
import java.util.Objects;

import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.enums.EvacuationType;

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
public class Evacuation {

    String evacuationNote;

    @ElementCollection
    @CollectionTable(name = "evacuation",
        joinColumns = @JoinColumn(name = "evacuation_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (evacuation_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<EvacuationType> evacuations;

    public static Evacuation initializeDefault() {
        Evacuation evacuation = new Evacuation();
        evacuation.evacuations = List.of();
        evacuation.evacuationNote = "";
        return evacuation;
    }

    public static Evacuation copy(Evacuation evacuation) {
        Evacuation copiedEvacuation = new Evacuation();
        copiedEvacuation.evacuations = List.copyOf(evacuation.evacuations);
        copiedEvacuation.evacuationNote = evacuation.evacuationNote;
        return copiedEvacuation;
    }

    public void update(EvacuationDTO evacuation) {
        if(Objects.isNull(evacuation)) {
            return;
        }

        this.evacuationNote = evacuation.evacuationNote();
        this.evacuations = evacuation.evacuations();
    }
}
