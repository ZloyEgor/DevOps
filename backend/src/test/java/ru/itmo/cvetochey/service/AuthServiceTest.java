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
import ru.itmo.cvetochey.dto.ClientDto;
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
    private ClientDto clientDto;
    private AuthRequestDto authRequestDto;

    @BeforeEach
    void setUp() {
        clientCreateDto = new ClientCreateDto();
        clientCreateDto.setEmail("test@example.com");
        clientCreateDto.setUsername("testuser");
        clientCreateDto.setPassword("password123");
        clientCreateDto.setUserRole(UserRole.CLIENT);

        client = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("encodedPassword")
            .userRole(UserRole.CLIENT)
            .build();

        clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setEmail("test@example.com");
        clientDto.setUsername("testuser");
        clientDto.setUserRole(UserRole.CLIENT);

        authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("test@example.com");
        authRequestDto.setPassword("password123");
    }

    @Test
    void register_ShouldCreateNewUser_WhenValidRequest() {
        // Given
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(jwtService.generateToken(any(ClientUserDetails.class))).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(ClientUserDetails.class))).thenReturn("refresh-token");
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

        // When
        AuthResponseDto result = authService.register(clientCreateDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals(clientDto, result.getUser());

        verify(clientRepository).existsByEmail("test@example.com");
        verify(clientRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(clientRepository).save(any(Client.class));
        verify(jwtService).generateToken(any(ClientUserDetails.class));
        verify(jwtService).generateRefreshToken(any(ClientUserDetails.class));
        verify(clientMapper).toDto(client);
    }

    @Test
    void register_ShouldUseDefaultRole_WhenRoleIsNull() {
        // Given
        clientCreateDto.setUserRole(null);
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(jwtService.generateToken(any(ClientUserDetails.class))).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(ClientUserDetails.class))).thenReturn("refresh-token");
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

        // When
        AuthResponseDto result = authService.register(clientCreateDto);

        // Then
        assertNotNull(result);
        verify(clientRepository).save(argThat(savedClient -> 
            savedClient.getUserRole() == UserRole.CLIENT));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Given
        when(clientRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(clientCreateDto));
        
        assertEquals("User with this email already exists", exception.getMessage());
        verify(clientRepository).existsByEmail("test@example.com");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(clientCreateDto));
        
        assertEquals("User with this username already exists", exception.getMessage());
        verify(clientRepository).existsByEmail("test@example.com");
        verify(clientRepository).existsByUsername("testuser");
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Given
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(client));
        when(jwtService.generateToken(any(ClientUserDetails.class))).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(ClientUserDetails.class))).thenReturn("refresh-token");
        when(clientMapper.toDto(any(Client.class))).thenReturn(clientDto);

        // When
        AuthResponseDto result = authService.authenticate(authRequestDto);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals(clientDto, result.getUser());

        verify(authenticationManager).authenticate(
            any(UsernamePasswordAuthenticationToken.class));
        verify(clientRepository).findByEmail("test@example.com");
        verify(jwtService).generateToken(any(ClientUserDetails.class));
        verify(jwtService).generateRefreshToken(any(ClientUserDetails.class));
        verify(clientMapper).toDto(client);
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(authRequestDto));
        
        assertEquals("User not found", exception.getMessage());
        verify(authenticationManager).authenticate(
            any(UsernamePasswordAuthenticationToken.class));
        verify(clientRepository).findByEmail("test@example.com");
    }

    @Test
    void authenticate_ShouldPropagateException_WhenAuthenticationFails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, 
            () -> authService.authenticate(authRequestDto));
        
        verify(authenticationManager).authenticate(
            any(UsernamePasswordAuthenticationToken.class));
        verify(clientRepository, never()).findByEmail(anyString());
    }
}
