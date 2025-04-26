package com.springboot.api.counselsession.dto.naverClova;

import java.util.List;

public record SegmentDTO(
    int start,
    int end,
    String text,
    double confidence,
    DiarizationDTO diarization,
    SpeakerDTO speaker,
    List<List<Object>> words,
    String textEdited) {

}
