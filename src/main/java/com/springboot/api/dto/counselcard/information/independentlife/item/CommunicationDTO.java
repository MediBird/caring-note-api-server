package com.springboot.api.dto.counselcard.information.independentlife.item;

import java.util.List;

import lombok.Builder;

@Builder
public record CommunicationDTO(
                List<String> sights, List<String> hearings, List<String> communications, List<String> usingKoreans) {
}
