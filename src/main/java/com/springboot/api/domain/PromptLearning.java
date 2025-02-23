package com.springboot.api.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "prompt_learnings")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, exclude = {"promptTemplate"})
@ToString(callSuper = true, exclude = {"promptTemplate"})
public class PromptLearning extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "prompt_template_id", nullable = false)
    private PromptTemplate promptTemplate;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String learningInputText;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String learningOutputText;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
