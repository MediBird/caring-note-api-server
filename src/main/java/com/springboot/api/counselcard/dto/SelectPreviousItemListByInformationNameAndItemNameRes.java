package com.springboot.api.counselcard.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

public record SelectPreviousItemListByInformationNameAndItemNameRes(LocalDate counselDate, JsonNode counselCardItem) {
}
