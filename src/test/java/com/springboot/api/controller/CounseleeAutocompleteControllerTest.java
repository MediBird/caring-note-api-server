package com.springboot.api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselee.controller.CounseleeController;
import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.service.CounseleeService;

@WebMvcTest(CounseleeController.class)
@Import({ SecurityConfig.class, TestSecurityConfig.class })
public class CounseleeAutocompleteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CounseleeService counseleeService;

        @MockBean
        private JwtDecoder jwtDecoder;

        @MockBean
        private CustomJwtRoleConverter customJwtRoleConverter;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void testAutocompleteWithHongKeyword() throws Exception {
                // 테스트 데이터 생성 - "홍"을 포함하는 10개의 이름
                List<SelectCounseleeAutocompleteRes> mockResults = Arrays.asList(
                                createMockAutocompleteRes("id1", "홍박사", LocalDate.of(1980, 1, 1)),
                                createMockAutocompleteRes("id2", "백홍준", LocalDate.of(1982, 2, 2)),
                                createMockAutocompleteRes("id3", "김지홍", LocalDate.of(1985, 3, 3)),
                                createMockAutocompleteRes("id4", "홍세달", LocalDate.of(1987, 4, 4)),
                                createMockAutocompleteRes("id5", "임달홍", LocalDate.of(1989, 5, 5)),
                                createMockAutocompleteRes("id6", "황비홍", LocalDate.of(1990, 6, 6)),
                                createMockAutocompleteRes("id7", "홍명보", LocalDate.of(1991, 7, 7)),
                                createMockAutocompleteRes("id8", "홍다는", LocalDate.of(1992, 8, 8)),
                                createMockAutocompleteRes("id9", "홍체념", LocalDate.of(1993, 9, 9)),
                                createMockAutocompleteRes("id10", "다홍치마", LocalDate.of(1995, 10, 10)));

                // 서비스 모킹
                when(counseleeService.searchCounseleesByName("홍")).thenReturn(mockResults);

                // 관리자 권한으로 설정
                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

                // API 호출 및 검증
                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "홍")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(10))
                                .andExpect(jsonPath("$.data[0].name").value("홍박사"))
                                .andExpect(jsonPath("$.data[1].name").value("백홍준"))
                                .andExpect(jsonPath("$.data[9].name").value("다홍치마"));
        }

        @Test
        public void testAutocompleteWithEmptyKeyword() throws Exception {
                // 빈 검색어로 검색 시 빈 목록 반환
                when(counseleeService.searchCounseleesByName("")).thenReturn(Collections.emptyList());

                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest()); // 유효성 검사 실패 예상 (최소 1자 이상)
        }

        @Test
        public void testAutocompleteWithNonExistentName() throws Exception {
                // 존재하지 않는 이름 검색 시 빈 목록 반환
                when(counseleeService.searchCounseleesByName("존재하지않는이름")).thenReturn(Collections.emptyList());

                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "존재하지않는이름")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        public void testAutocompleteWithRoleUser() throws Exception {
                // 일반 사용자 권한으로 검색
                List<SelectCounseleeAutocompleteRes> mockResults = Arrays.asList(
                                createMockAutocompleteRes("id1", "김철수", LocalDate.of(1980, 1, 1)),
                                createMockAutocompleteRes("id2", "김영희", LocalDate.of(1982, 2, 2)));

                when(counseleeService.searchCounseleesByName("김")).thenReturn(mockResults);

                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "김")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].name").value("김철수"))
                                .andExpect(jsonPath("$.data[1].name").value("김영희"));
        }

        @Test
        public void testAutocompleteWithInvalidRole() throws Exception {
                // 권한 없는 사용자로 요청 시 403 응답
                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_INVALID")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "김")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testAutocompleteWithOverSizedKeyword() throws Exception {
                // 최대 길이 초과 검색어로 요청 시 400 응답
                String longKeyword = "a".repeat(51); // 51자

                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", longKeyword)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        // 한글, 영어, 특수문자 혼합 테스트
        @Test
        public void testAutocompleteWithMixedCharacters() throws Exception {
                // 한글, 영어 혼합된 이름 검색
                List<SelectCounseleeAutocompleteRes> mockResults = Arrays.asList(
                                createMockAutocompleteRes("id1", "John김", LocalDate.of(1980, 1, 1)),
                                createMockAutocompleteRes("id2", "김Smith", LocalDate.of(1982, 2, 2)));

                when(counseleeService.searchCounseleesByName("김")).thenReturn(mockResults);

                mockJwtToken(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

                mockMvc.perform(get("/v1/counsel/counselee/autocomplete")
                                .param("keyword", "김")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2));
        }

        // 모의 자동완성 응답 객체 생성 헬퍼 메서드
        private SelectCounseleeAutocompleteRes createMockAutocompleteRes(String id, String name, LocalDate birthDate) {
                return SelectCounseleeAutocompleteRes.builder()
                                .counseleeId(id)
                                .name(name)
                                .birthDate(birthDate)
                                .build();
        }

        private void mockJwtToken(Collection<GrantedAuthority> authorities) {
                Jwt jwt = Jwt.withTokenValue("token")
                                .header("alg", "none")
                                .claim("sub", "user")
                                .build();

                when(jwtDecoder.decode(anyString())).thenReturn(jwt);
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);
        }
}