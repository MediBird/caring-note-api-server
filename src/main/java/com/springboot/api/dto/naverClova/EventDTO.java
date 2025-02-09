package com.springboot.api.dto.naverClova;

public record EventDTO(
        String type,
        String label,
        String labelEdited,
        int start,
        int end
){}
