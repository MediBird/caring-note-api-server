package com.springboot.api.counselsession.dto.aiCounselSummary;


public record SelectSpeakerListRes(
        String speaker, String text, boolean isRecommended) {

    public static SelectSpeakerListRes of(String speaker,SpeakerStatsDTO speakerStats, int totalSpeakCount) {
        return new SelectSpeakerListRes(speaker, speakerStats.getMaxLengthText(), speakerStats.isRecommendedSpeaker(totalSpeakCount));
    }
}
