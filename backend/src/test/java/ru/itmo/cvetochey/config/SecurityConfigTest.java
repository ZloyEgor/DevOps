package ru.itmo.cvetochey.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authConfig;

    @Mock
    private AuthenticationManager authManager;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);
    }

    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        // When
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        // Then
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
        
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;
        // Note: We can't easily verify the internal state of DaoAuthenticationProvider
        // but we can verify it was created successfully
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        // Given
        when(authConfig.getAuthenticationManager()).thenReturn(authManager);

        // When
        AuthenticationManager result = securityConfig.authenticationManager(authConfig);

        // Then
        assertEquals(authManager, result);
        verify(authConfig).getAuthenticationManager();
    }

    @Test
    void passwordEncoder_ShouldReturnCustomPasswordEncoder() {
        // When
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Then
        assertNotNull(encoder);
        
        // Test encoding
        String rawPassword = "testPassword";
        String encoded = encoder.encode(rawPassword);
        assertEquals(rawPassword, encoded);
        
        // Test matching
        assertTrue(encoder.matches(rawPassword, encoded));
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    void passwordEncoder_ShouldHandleNullInput() {
        // Given
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            encoder.encode(null);
        });
    }

    @Test
    void passwordEncoder_ShouldHandleEmptyString() {
        // Given
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // When
        String encoded = encoder.encode("");
        boolean matches = encoder.matches("", encoded);

        // Then
        assertEquals("", encoded);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_ShouldHandleSpecialCharacters() {
        // Given
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String specialPassword = "p@ssw0rd!#$%";

        // When
        String encoded = encoder.encode(specialPassword);
        boolean matches = encoder.matches(specialPassword, encoded);

        // Then
        assertEquals(specialPassword, encoded);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_ShouldBeCaseSensitive() {
        // Given
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String password = "Password123";

        // When
        String encoded = encoder.encode(password);

        // Then
        assertTrue(encoder.matches(password, encoded));
        assertFalse(encoder.matches("password123", encoded));
        assertFalse(encoder.matches("PASSWORD123", encoded));
    }
}
