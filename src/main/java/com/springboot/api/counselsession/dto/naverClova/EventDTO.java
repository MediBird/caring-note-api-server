package com.springboot.api.counselsession.dto.naverClova;

public record EventDTO(
    String type,
    String label,
    String labelEdited,
    int start,
    int end) {

}
