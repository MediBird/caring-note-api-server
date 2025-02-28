package com.springboot.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "prompt_learnings")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, exclude = { "promptTemplate" })
@ToString(callSuper = true, exclude = { "promptTemplate" })
public class PromptLearning extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "prompt_template_id", nullable = false)
    private PromptTemplate promptTemplate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String learningInputText;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String learningOutputText;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
