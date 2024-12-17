package com.springboot.api.common.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.exception.JsonConvertException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonStringConverter implements AttributeConverter<JsonNode, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {

        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);  // Map을 JSON 문자열로 변환
        } catch (Exception e) {
            throw new JsonConvertException();
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {

        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(dbData);
        } catch (Exception e) {

            throw new JsonConvertException();
        }
    }
}
