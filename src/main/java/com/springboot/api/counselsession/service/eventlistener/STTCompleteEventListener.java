package com.springboot.api.counselsession.service.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.counselsession.service.AICounselSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class STTCompleteEventListener {
    private final AICounselSummaryService aiCounselSummaryService;

    @Async
    @EventListener
    public void handleSTTCompleted(STTCompleteEvent sttCompleteEvent) throws JsonProcessingException {
        aiCounselSummaryService.analyseText(sttCompleteEvent.counselSessionId());
    }
}
