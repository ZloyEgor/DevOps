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
    private ClientUserDetailsService userDetailsService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("+1234567890")
            .role(UserRole.CLIENT)
            .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testClient));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        
        // Check authorities
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
        
        verify(clientRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));
        
        verify(clientRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnAdminRole_WhenUserIsAdmin() {
        // Given
        Client adminClient = Client.builder()
            .id(2L)
            .email("admin@example.com")
            .password("encodedPassword")
            .firstName("Admin")
            .lastName("User")
            .role(UserRole.ADMIN)
            .build();
        
        when(clientRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminClient));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        
        // Check authorities
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        
        verify(clientRepository).findByEmail("admin@example.com");
    }

    @Test
    void loadUserByUsername_ShouldHandleNullEmail() {
        // When & Then
        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsername_ShouldHandleEmptyEmail() {
        // Given
        when(clientRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(""));
        
        verify(clientRepository).findByEmail("");
    }
}
