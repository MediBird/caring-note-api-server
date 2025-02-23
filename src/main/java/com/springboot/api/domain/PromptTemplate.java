package com.springboot.api.domain;

import com.springboot.enums.PromptTemplateType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
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

    private static final Logger log = LoggerFactory.getLogger(PromptTemplate.class);
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

    public List<Message> generatePromptMessages(UserMessage userMessage) {

        ArrayList<Message> messages = new ArrayList<>();

        if(PromptTemplateType.SYSTEM.equals(this.promptTemplateType)) {
            messages.add(new SystemMessage(promptText));
        }
        else{
            messages.add(new UserMessage(promptText));
        }

        for(PromptLearning promptLearning : promptLearnings) {
            StringBuilder learingStringBuilder = new StringBuilder();

            if (!StringUtils.isBlank(promptLearning.getLearningInputText())) {
                learingStringBuilder.append("input: \n");
                learingStringBuilder.append(promptLearning.getLearningInputText());
            }
            if(!StringUtils.isBlank(promptLearning.getLearningOutputText()))
            {
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
