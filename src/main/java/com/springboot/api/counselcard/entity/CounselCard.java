package com.springboot.api.counselcard.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.converter.JsonStringConverter;
import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.CardRecordStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "counsel_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession" })
@ToString(callSuper = true, exclude = { "counselSession" })
public class CounselCard extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
