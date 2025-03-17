package com.springboot.api.counselsession.entity;

import com.springboot.api.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "prompt_learnings")
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "promptTemplate" })
@ToString(callSuper = true, exclude = { "promptTemplate" })
public class PromptLearning extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "prompt_template_id", nullable = false)
    @SuppressWarnings("FieldMayBeFinal")
    private PromptTemplate promptTemplate;

    @Column(columnDefinition = "TEXT", nullable = false)
    @SuppressWarnings("FieldMayBeFinal")
    private String learningInputText;

    @Column(columnDefinition = "TEXT", nullable = false)
    @SuppressWarnings("FieldMayBeFinal")
    private String learningOutputText;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
