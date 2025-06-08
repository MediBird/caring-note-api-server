package com.springboot.api.common.util;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.exception.NoContentException;

class AiResponseParseUtilTest {

    private AiResponseParseUtil aiResponseParseUtil;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        aiResponseParseUtil = new AiResponseParseUtil();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("정상적인 AI 응답에서 텍스트를 추출할 수 있다")
    void extractAnalysedText_ValidResponse_Success() throws Exception {
        // given
        String jsonResponse = """
            {
                "result": {
                    "output": {
                        "text": "분석된 상담 내용입니다."
                    }
                }
            }
            """;
        JsonNode taResult = objectMapper.readTree(jsonResponse);

        // when
        String result = aiResponseParseUtil.extractAnalysedText(taResult);

        // then
        assertThat(result).isEqualTo("분석된 상담 내용입니다.");
    }

    @Test
    @DisplayName("null JsonNode에서는 NoContentException이 발생한다")
    void extractAnalysedText_NullNode_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> aiResponseParseUtil.extractAnalysedText(null))
            .isInstanceOf(NoContentException.class);
    }

    @Test
    @DisplayName("잘못된 구조의 JsonNode에서는 NoContentException이 발생한다")
    void extractAnalysedText_InvalidStructure_ThrowsException() throws Exception {
        // given
        String jsonResponse = """
            {
                "invalid": {
                    "structure": "test"
                }
            }
            """;
        JsonNode taResult = objectMapper.readTree(jsonResponse);

        // when & then
        assertThatThrownBy(() -> aiResponseParseUtil.extractAnalysedText(taResult))
            .isInstanceOf(NoContentException.class);
    }

    @Test
    @DisplayName("안전한 추출에서는 예외 대신 Optional.empty()를 반환한다")
    void extractAnalysedTextSafely_InvalidStructure_ReturnsEmpty() throws Exception {
        // given
        String jsonResponse = """
            {
                "invalid": "structure"
            }
            """;
        JsonNode taResult = objectMapper.readTree(jsonResponse);

        // when
        Optional<String> result = aiResponseParseUtil.extractAnalysedTextSafely(taResult);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 텍스트는 유효하지 않은 응답으로 처리된다")
    void extractAnalysedText_EmptyText_ThrowsException() throws Exception {
        // given
        String jsonResponse = """
            {
                "result": {
                    "output": {
                        "text": "   "
                    }
                }
            }
            """;
        JsonNode taResult = objectMapper.readTree(jsonResponse);

        // when & then
        assertThatThrownBy(() -> aiResponseParseUtil.extractAnalysedText(taResult))
            .isInstanceOf(NoContentException.class);
    }

    @Test
    @DisplayName("유효한 AI 응답인지 검증할 수 있다")
    void isValidAiResponse_ValidResponse_ReturnsTrue() throws Exception {
        // given
        String jsonResponse = """
            {
                "result": {
                    "output": {
                        "text": "유효한 텍스트"
                    }
                }
            }
            """;
        JsonNode taResult = objectMapper.readTree(jsonResponse);

        // when
        boolean result = aiResponseParseUtil.isValidAiResponse(taResult);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 AI 응답은 false를 반환한다")
    void isValidAiResponse_InvalidResponse_ReturnsFalse() {
        // when & then
        assertThat(aiResponseParseUtil.isValidAiResponse(null)).isFalse();
    }
} 