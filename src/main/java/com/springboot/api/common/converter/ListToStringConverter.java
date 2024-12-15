package com.springboot.api.common.converter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.exception.JsonConvertException;
import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper;

    public ListToStringConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(List<String> attribute) throws RuntimeException {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new JsonConvertException();
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) throws RuntimeException {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new JsonConvertException();
        }
    }
}
