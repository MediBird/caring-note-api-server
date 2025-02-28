package com.springboot.api.dto.naverClova;

public record UserDataDTO(
                String _ncp_DomainCode,
                int _ncp_DomainId,
                int _ncp_TaskId,
                String _ncp_TraceId,
                int id) {
}