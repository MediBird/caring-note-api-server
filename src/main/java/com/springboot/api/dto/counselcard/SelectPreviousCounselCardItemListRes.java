package com.springboot.api.dto.counselcard;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;

public record SelectPreviousCounselCardItemListRes(LocalDate counselDate, JsonNode counselCardItem) {}
