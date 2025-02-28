package com.springboot.api.counselsession.entity;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.converter.JsonStringConverter;
import com.springboot.api.common.converter.ListStringConverter;
import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.enums.AICounselSummaryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "ai_counsel_summarys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession" })
@ToString(callSuper = true, exclude = { "counselSession" })
public class AICounselSummary extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Column(name = "stt_result", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode sttResult;

    @Column(name = "ta_result", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode taResult;

    @Column(name = "ai_counsel_summary_status")
    @Enumerated(EnumType.STRING)
    private AICounselSummaryStatus aiCounselSummaryStatus;

    @Column(name = "speakers", columnDefinition = "TEXT")
    @Convert(converter = ListStringConverter.class)
    private List<String> speakers;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
