package com.springboot.api.domain;


import com.springboot.enums.CardRecordStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

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


    @Column(name = "base_information", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> baseInformation;

    @Column(name = "health_information", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> healthInformation;

    @Column(name = "living_information", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> livingInformation;

    @Column(name = "independent_life_information", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> independentLifeInformation;

    @Column
    @Enumerated(EnumType.STRING)
    private CardRecordStatus cardRecordStatus;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }


}
