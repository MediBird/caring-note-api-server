package com.springboot.api.common.converter;

import java.util.Arrays;
import java.util.List;

import com.springboot.api.common.exception.JsonConvertException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ListStringConverter implements AttributeConverter<List<String>, String> {

    private final String delimiter = "\\^=\\^";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) throws RuntimeException {
        try {
            if (attribute == null) {
                return null;
            }

            return String.join(delimiter.replace("\\", ""), attribute);

        } catch (Exception e) {
            throw new JsonConvertException();
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) throws RuntimeException {
        try {

            if (dbData == null) {
                return List.of();
            }

            return Arrays.stream(dbData.split(delimiter))
                .filter(s -> !s.isEmpty()) // 빈 문자열 필터링
                .toList();

        } catch (Exception e) {
            throw new JsonConvertException();
        }
    }
}
