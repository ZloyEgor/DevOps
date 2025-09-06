package ru.itmo.cvetochey.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientUserDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientUserDetailsService clientUserDetailsService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("encodedPassword")
            .userRole(UserRole.CLIENT)
            .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testClient));

        // When
        UserDetails userDetails = clientUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(clientRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> clientUserDetailsService.loadUserByUsername("nonexistent@example.com")
        );

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(clientRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnAdminRole_WhenUserIsAdmin() {
        // Given
        Client adminClient = Client.builder()
            .id(2L)
            .email("admin@example.com")
            .username("admin")
            .password("adminPassword")
            .userRole(UserRole.ADMIN)
            .build();
        
        when(clientRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminClient));

        // When
        UserDetails userDetails = clientUserDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(clientRepository).findByEmail("admin@example.com");
    }
}
