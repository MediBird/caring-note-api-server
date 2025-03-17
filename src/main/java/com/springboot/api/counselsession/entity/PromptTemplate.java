package com.springboot.api.counselsession.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.enums.PromptTemplateType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "prompt_templates")
@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "promptLearnings" })
@ToString(callSuper = true, exclude = { "promptLearnings" })
public class PromptTemplate extends BaseEntity {

    private static final Logger log = LoggerFactory.getLogger(PromptTemplate.class);
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptTemplateType promptTemplateType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String promptText;

    @OneToMany(mappedBy = "promptTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PromptLearning> promptLearnings;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public List<Message> generatePromptMessages(UserMessage userMessage) {

        ArrayList<Message> messages = new ArrayList<>();

        if (PromptTemplateType.SYSTEM.equals(this.promptTemplateType)) {
            messages.add(new SystemMessage(promptText));
        } else {
            messages.add(new UserMessage(promptText));
        }

        for (PromptLearning promptLearning : promptLearnings) {
            StringBuilder learingStringBuilder = new StringBuilder();

            if (!StringUtils.isBlank(promptLearning.getLearningInputText())) {
                learingStringBuilder.append("input: \n");
                learingStringBuilder.append(promptLearning.getLearningInputText());
            }
            if (!StringUtils.isBlank(promptLearning.getLearningOutputText())) {
                learingStringBuilder.append("output: \n");
                learingStringBuilder.append(promptLearning.getLearningOutputText());
            }
            messages.add(new AssistantMessage(learingStringBuilder.toString()));
        }

        messages.add(userMessage);

        log.info(messages.toString());
        return messages;
    }

}
