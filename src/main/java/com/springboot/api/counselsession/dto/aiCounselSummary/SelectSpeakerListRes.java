package com.springboot.api.counselsession.dto.aiCounselSummary;


public record SelectSpeakerListRes(
        String speaker, String text) {

    public static SelectSpeakerListRes of(String speaker, String text) {
        return new SelectSpeakerListRes(speaker, text);
    }
}
