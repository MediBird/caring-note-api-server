// package com.springboot.api.controller;

// import static org.hamcrest.Matchers.startsWith;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import org.springframework.test.web.servlet.MockMvc;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.springboot.api.common.util.JwtUtil;
// import com.springboot.api.dto.counselor.AddCounselorReq;
// import com.springboot.api.dto.counselor.AddCounselorRes;
// import com.springboot.api.dto.counselor.LoginCounselorReq;
// import com.springboot.api.service.CounselorService;
// import com.springboot.enums.RoleType;

// @AutoConfigureMockMvc
// @SpringBootTest
// public class CounselorControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private JwtUtil jwtUtil;

//     @MockBean
//     private CounselorService counselorService;

//     @Test
//     void shouldAddCounselorSuccessfully() throws Exception {
//         Mockito.when(counselorService.addCounselor(any(AddCounselorReq.class)))
//                 .thenReturn(new AddCounselorRes("01HXBK6P4NCZXCSNAG3FY7YZDT", RoleType.ROLE_ADMIN));

//         AddCounselorReq request = AddCounselorReq.builder()
//                 .name("sunpil")
//                 .email("roung4119@gmail.com")
//                 .phoneNumber("01090654118")
//                 .password("12341234")
//                 .roleType(RoleType.ROLE_ADMIN)
//                 .build();

//         // When: 요청 수행
//         mockMvc.perform(post("/v1/counselor/signup").with(csrf())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//                 .andDo(result -> {
//                     System.out.println("Response Status: " + result.getResponse().getStatus());
//                     System.out.println("Response Headers: " + result.getResponse().getHeaderNames());
//                 })
//                 .andExpect(status().isCreated())
//                 .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));
//     }

//     @Test
//     void shouldAddCounselorAndLoginSuccessfully() throws Exception {
//         Mockito.when(counselorService.addCounselor(any(AddCounselorReq.class)))
//                 .thenReturn(new AddCounselorRes("01HXBK6P4NCZXCSNAG3FY7YZDT", RoleType.ROLE_ADMIN));

//         AddCounselorReq signupRequest = AddCounselorReq.builder()
//                 .name("sunpil.choi")
//                 .email("roung4118@gmail.com")
//                 .phoneNumber("01090664118")
//                 .password("12341234")
//                 .roleType(RoleType.ROLE_ADMIN)
//                 .build();

//         LoginCounselorReq loginRequest = LoginCounselorReq.builder()
//                 .email("roung4118@gmail.com")
//                 .password("12341234")
//                 .build();

//         // When: Counselor Signup
//         mockMvc.perform(post("/v1/counselor/signup").with(csrf())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(signupRequest)))
//                 // Then: 응답 검증
//                 .andExpect(status().isCreated())
//                 .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));

//         // When: Counselor Login
//         mockMvc.perform(post("/v1/counselor/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(loginRequest)))
//                 // Then: 응답 검증
//                 .andExpect(status().isCreated())
//                 .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));
//     }
// }
