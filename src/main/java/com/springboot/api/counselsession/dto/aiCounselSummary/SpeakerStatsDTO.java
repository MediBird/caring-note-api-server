package com.springboot.api.counselsession.dto.aiCounselSummary;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeakerStatsDTO {

    private int speakCount;
    private String maxLengthText;
    private int textLengthSum;
    private int maxLengthTextLength;

    public void updateSpeakerStats(String text) {
        textLengthSum += text.length();
        speakCount++;

        if (text.length() > maxLengthTextLength) {
            maxLengthText = text;
            maxLengthTextLength = text.length();
        }
    }

    public boolean isValidSpeaker() {

        return speakCount >= 3 && maxLengthTextLength >= 3;
    }

    public boolean isRecommendedSpeaker(int totalSpeakCount) {

        if (totalSpeakCount == 0) {
            return false;
        }
        return ((float) speakCount / totalSpeakCount) > 0.3;

    }

}
