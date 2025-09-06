package ru.itmo.cvetochey.controller.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.cvetochey.dto.AuthRequestDto;
import ru.itmo.cvetochey.dto.AuthResponseDto;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.service.AuthService;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientCreateDto clientCreateDto;
    private AuthRequestDto authRequestDto;
    private AuthResponseDto authResponseDto;

    @BeforeEach
    void setUp() {
        clientCreateDto = ClientCreateDto.builder()
            .email("test@example.com")
            .password("password123")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("+1234567890")
            .build();

        authRequestDto = AuthRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        authResponseDto = AuthResponseDto.builder()
            .token("jwt-token")
            .build();
    }

    @Test
    void register_ShouldReturnOk_WhenValidInput() throws Exception {
        // Given
        when(authService.register(any(ClientCreateDto.class))).thenReturn(authResponseDto);

        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientCreateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Given
        when(authService.register(any(ClientCreateDto.class)))
            .thenThrow(new RuntimeException("Email already exists"));

        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientCreateDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnOk_WhenValidCredentials() throws Exception {
        // Given
        when(authService.authenticate(any(AuthRequestDto.class))).thenReturn(authResponseDto);

        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        // Given
        when(authService.authenticate(any(AuthRequestDto.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequestDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void logout_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/logout"))
            .andExpect(status().isOk());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/cvet-ochey/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
