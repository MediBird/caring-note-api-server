package com.springboot.api.dto.counselsession;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SelectCounselSessionListItem {
    private String id;
    private String scheduledTime;
    private String scheduledDate;
    private String counseleeName;
    private String counselorName;
    private boolean isShardCaringMessage;
}
