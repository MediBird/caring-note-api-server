package com.springboot.api.domain;

import com.springboot.enums.PromptTemplateType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "prompt_templates")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, exclude = {"promptLearnings"})
@ToString(callSuper = true, exclude = {"promptLearnings"})
public class PromptTemplate extends BaseEntity{

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptTemplateType promptTemplateType;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String promptText;

    @OneToMany(mappedBy = "promptTemplate", cascade = CascadeType.ALL)
    private List<PromptLearning> promptLearnings;


    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
