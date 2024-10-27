package com.springboot.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.ApiApplication;
import com.springboot.api.domain.RoleType;
import com.springboot.api.dto.user.AddUserReq;

import com.springboot.api.dto.user.AddUserRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiApplication.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void addUser_ShouldReturnCreatedWithToken() throws Exception {

        AddUserReq addUserReq = new AddUserReq("testuser@example.com"
                , "password"
                ,"test"
                , List.of(RoleType.ADMIN));

        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserReq))
                )
                .andExpect(status().isCreated());
    }
}
