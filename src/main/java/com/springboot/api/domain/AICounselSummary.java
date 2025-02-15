package com.springboot.api.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.converter.JsonStringConverter;
import com.springboot.api.common.converter.ListStringConverter;
import com.springboot.enums.AICounselSummaryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ai_counsel_summarys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class AICounselSummary extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Column(name = "stt_result", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode sttResult;

    @Column(name ="ta_result", columnDefinition = "text")
    @Convert(converter = JsonStringConverter.class)
    private JsonNode taResult;

    @Column(name= "ai_counsel_summary_status")
    @Enumerated(EnumType.STRING)
    private AICounselSummaryStatus aiCounselSummaryStatus;

    @Column(name= "speakers", columnDefinition = "TEXT")
    @Convert(converter = ListStringConverter.class)
    private List<String> speakers;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
