package com.springboot.api.domain;

import com.springboot.api.common.converter.ListStringConverter;
import com.springboot.enums.CounselNeedStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medication_counsels")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class MedicationCounsel extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Lob
    private String counselRecord;


    @Convert(converter = ListStringConverter.class)
    @Column(name= "counsel_record_highlights", columnDefinition = "TEXT")
    private List<String> counselRecordHighlights;


    @Enumerated(EnumType.STRING)
    private CounselNeedStatus counselNeedStatus;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
