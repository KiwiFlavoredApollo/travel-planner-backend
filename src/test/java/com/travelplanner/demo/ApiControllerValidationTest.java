package com.travelplanner.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.demo.user.controller.UserController;
import com.travelplanner.demo.user.dto.RegisterRequest;
import com.travelplanner.demo.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiControllerValidationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService = Mockito.mock(UserService.class);

    private MockMvc getMockMvcWithValidation() {
        UserController userController = new UserController(userService);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet(); // 수동 초기화

        return MockMvcBuilders.standaloneSetup(userController)
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("[API 검증] 회원가입 시 아이디 글자수 초과(20자 초과) 시 400 Bad Request 에러 반환")
    void registerValidation_OverLengthUserId_Returns400() throws Exception {
        mockMvc = getMockMvcWithValidation();

        // Given (21글자의 유효하지 않은 ID)
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .userId("abcdefghijklmnopqrstux") // 21 chars
                .password("password123")
                .name("테스터")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[API 검증] 회원가입 시 공백 입력(Blank) 시 400 Bad Request 에러 반환")
    void registerValidation_BlankUserId_Returns400() throws Exception {
        mockMvc = getMockMvcWithValidation();

        // Given
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .userId("") // Blank
                .password("password123")
                .name("테스터")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}