package com.springboot.api.counselsession.dto.naverClova;

import java.util.List;
import java.util.Map;

public record SpeechToTextRes(String result,
                              String message,
                              String token,
                              String version,
                              ParamsDTO params,
                              int progress,
                              Map<String, Object> keywords,
                              List<SegmentDTO> segments,
                              String text,
                              double confidence,
                              List<SpeakerDTO> speakers,
                              List<EventDTO> events,
                              List<String> eventTypes) {

}
