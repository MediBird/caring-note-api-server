package com.springboot.api.common.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;

import lombok.extern.slf4j.Slf4j;

/**
 * AI 응답 파싱을 위한 유틸리티 클래스
 * ChatResponse JSON 구조에서 안전하게 데이터를 추출합니다.
 */
@Component
@Slf4j
public class AiResponseParseUtil {

    private static final String RESULT_PATH = "result";
    private static final String OUTPUT_PATH = "output";
    private static final String TEXT_PATH = "text";

    /**
     * ChatResponse JsonNode에서 분석된 텍스트를 안전하게 추출합니다.
     * 
     * @param taResult ChatResponse가 JSON으로 변환된 JsonNode
     * @return 분석된 텍스트
     * @throws NoContentException taResult가 null이거나 텍스트를 찾을 수 없는 경우
     */
    public String extractAnalysedText(JsonNode taResult) {
        if (taResult == null || taResult.isNull()) {
            log.warn("taResult is null or empty");
            throw new NoContentException();
        }

        return Optional.ofNullable(taResult.get(RESULT_PATH))
            .map(result -> result.get(OUTPUT_PATH))
            .map(output -> output.get(TEXT_PATH))
            .filter(text -> !text.isNull())
            .map(JsonNode::asText)
            .filter(text -> !text.trim().isEmpty())
            .orElseThrow(() -> {
                log.warn("Failed to extract text from taResult: missing result.output.text path");
                return new NoContentException();
            });
    }

    /**
     * ChatResponse JsonNode에서 분석된 텍스트를 안전하게 추출합니다. (Optional 반환)
     * 
     * @param taResult ChatResponse가 JSON으로 변환된 JsonNode
     * @return 분석된 텍스트 Optional
     */
    public Optional<String> extractAnalysedTextSafely(JsonNode taResult) {
        try {
            return Optional.of(extractAnalysedText(taResult));
        } catch (NoContentException e) {
            log.debug("Could not extract analysed text from taResult", e);
            return Optional.empty();
        }
    }

    /**
     * JsonNode가 유효한 AI 분석 결과인지 검증합니다.
     * 
     * @param taResult 검증할 JsonNode
     * @return 유효한 AI 분석 결과인지 여부
     */
    public boolean isValidAiResponse(JsonNode taResult) {
        if (taResult == null || taResult.isNull()) {
            return false;
        }

        JsonNode result = taResult.get(RESULT_PATH);
        if (result == null || result.isNull()) {
            return false;
        }

        JsonNode output = result.get(OUTPUT_PATH);
        if (output == null || output.isNull()) {
            return false;
        }

        JsonNode text = output.get(TEXT_PATH);
        return text != null && !text.isNull() && !text.asText().trim().isEmpty();
    }
} 