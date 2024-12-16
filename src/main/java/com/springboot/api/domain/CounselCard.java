package com.springboot.api.domain;


import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.converter.JsonStringConverter;
import com.springboot.enums.CardRecordStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "counsel_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class CounselCard extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;


    @Column(name = "base_information", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode baseInformation;

    @Column(name = "health_information", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode healthInformation;

    @Column(name = "living_information", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode livingInformation;

    @Column(name = "independent_life_information", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode independentLifeInformation;

    @Column
    @Enumerated(EnumType.STRING)
    private CardRecordStatus cardRecordStatus;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }


}
