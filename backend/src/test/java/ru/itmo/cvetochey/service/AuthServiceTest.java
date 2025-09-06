package ru.itmo.cvetochey.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.cvetochey.dto.AuthRequestDto;
import ru.itmo.cvetochey.dto.AuthResponseDto;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.mapper.ClientMapper;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private AuthService authService;

    private ClientCreateDto clientCreateDto;
    private Client client;
    private AuthRequestDto authRequestDto;

    @BeforeEach
    void setUp() {
        clientCreateDto = ClientCreateDto.builder()
            .email("test@example.com")
            .password("password123")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("+1234567890")
            .build();

        client = Client.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("+1234567890")
            .role(UserRole.CLIENT)
            .build();

        authRequestDto = AuthRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenValidInput() {
        // Given
        when(clientRepository.existsByEmail(clientCreateDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(clientCreateDto.getPassword())).thenReturn("encodedPassword");
        when(clientMapper.toEntity(clientCreateDto)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(jwtService.generateToken(client)).thenReturn("jwt-token");

        // When
        AuthResponseDto result = authService.register(clientCreateDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        verify(clientRepository).existsByEmail(clientCreateDto.getEmail());
        verify(passwordEncoder).encode(clientCreateDto.getPassword());
        verify(clientRepository).save(any(Client.class));
        verify(jwtService).generateToken(client);
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(clientRepository.existsByEmail(clientCreateDto.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.register(clientCreateDto));
        verify(clientRepository).existsByEmail(clientCreateDto.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Given
        when(clientRepository.findByEmail(authRequestDto.getEmail())).thenReturn(Optional.of(client));
        when(jwtService.generateToken(client)).thenReturn("jwt-token");

        // When
        AuthResponseDto result = authService.authenticate(authRequestDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequestDto.getEmail(),
                authRequestDto.getPassword()
            )
        );
        verify(clientRepository).findByEmail(authRequestDto.getEmail());
        verify(jwtService).generateToken(client);
    }

    @Test
    void authenticate_ShouldThrowException_WhenInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequestDto));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(clientRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(Client.class));
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(clientRepository.findByEmail(authRequestDto.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequestDto));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(clientRepository).findByEmail(authRequestDto.getEmail());
        verify(jwtService, never()).generateToken(any(Client.class));
    }
}
