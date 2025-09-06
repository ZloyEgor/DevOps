package ru.itmo.cvetochey.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.itmo.cvetochey.dto.AuthRequestDto;
import ru.itmo.cvetochey.dto.AuthResponseDto;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ClientCreateDto clientCreateDto;
    private AuthRequestDto authRequestDto;
    private AuthResponseDto authResponseDto;

    @BeforeEach
    void setUp() {
        clientCreateDto = new ClientCreateDto();
        clientCreateDto.setEmail("test@example.com");
        clientCreateDto.setPassword("password123");
        clientCreateDto.setUsername("johndoe");
        clientCreateDto.setUserRole(UserRole.CLIENT);

        authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("test@example.com");
        authRequestDto.setPassword("password123");

        authResponseDto = new AuthResponseDto();
        authResponseDto.setToken("jwt-token-123");
    }

    @Test
    void register_ShouldReturnAuthResponse() {
        // Given
        when(authService.register(any(ClientCreateDto.class))).thenReturn(authResponseDto);

        // When
        ResponseEntity<AuthResponseDto> result = authController.register(clientCreateDto);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(authResponseDto, result.getBody());
        verify(authService).register(clientCreateDto);
    }

    @Test
    void register_WhenServiceThrowsException_ShouldReturnBadRequest() {
        // Given
        when(authService.register(any(ClientCreateDto.class)))
            .thenThrow(new RuntimeException("Email already exists"));

        // When
        ResponseEntity<AuthResponseDto> result = authController.register(clientCreateDto);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
        verify(authService).register(clientCreateDto);
    }

    @Test
    void authenticate_ShouldReturnAuthResponse() {
        // Given
        when(authService.authenticate(any(AuthRequestDto.class))).thenReturn(authResponseDto);

        // When
        ResponseEntity<AuthResponseDto> result = authController.authenticate(authRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(authResponseDto, result.getBody());
        verify(authService).authenticate(authRequestDto);
    }

    @Test
    void authenticate_WhenServiceThrowsException_ShouldReturnBadRequest() {
        // Given
        when(authService.authenticate(any(AuthRequestDto.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // When
        ResponseEntity<AuthResponseDto> result = authController.authenticate(authRequestDto);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
        verify(authService).authenticate(authRequestDto);
    }

    @Test
    void register_WithInvalidEmail_ShouldReturnBadRequest() {
        // Given
        clientCreateDto.setEmail("invalid-email");
        when(authService.register(any(ClientCreateDto.class)))
            .thenThrow(new IllegalArgumentException("Invalid email format"));

        // When
        ResponseEntity<AuthResponseDto> result = authController.register(clientCreateDto);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void authenticate_WithWrongPassword_ShouldReturnBadRequest() {
        // Given
        authRequestDto.setPassword("wrongpassword");
        when(authService.authenticate(any(AuthRequestDto.class)))
            .thenThrow(new RuntimeException("Authentication failed"));

        // When
        ResponseEntity<AuthResponseDto> result = authController.authenticate(authRequestDto);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }
}
