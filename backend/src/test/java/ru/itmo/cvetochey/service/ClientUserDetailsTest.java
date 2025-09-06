package ru.itmo.cvetochey.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;

class ClientUserDetailsTest {

    private Client testClient;
    private ClientUserDetails clientUserDetails;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("encodedPassword")
            .userRole(UserRole.CLIENT)
            .build();
        
        clientUserDetails = new ClientUserDetails(testClient);
    }

    @Test
    void getAuthorities_ShouldReturnClientRole() {
        // When
        Collection<? extends GrantedAuthority> authorities = clientUserDetails.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    void getAuthorities_ShouldReturnAdminRole_WhenUserIsAdmin() {
        // Given
        Client adminClient = Client.builder()
            .id(2L)
            .email("admin@example.com")
            .username("admin")
            .password("adminPassword")
            .userRole(UserRole.ADMIN)
            .build();
        
        ClientUserDetails adminUserDetails = new ClientUserDetails(adminClient);

        // When
        Collection<? extends GrantedAuthority> authorities = adminUserDetails.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void getPassword_ShouldReturnClientPassword() {
        // When
        String password = clientUserDetails.getPassword();

        // Then
        assertEquals("encodedPassword", password);
    }

    @Test
    void getUsername_ShouldReturnClientEmail() {
        // When
        String username = clientUserDetails.getUsername();

        // Then
        assertEquals("test@example.com", username);
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        // When
        boolean isAccountNonExpired = clientUserDetails.isAccountNonExpired();

        // Then
        assertTrue(isAccountNonExpired);
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        // When
        boolean isAccountNonLocked = clientUserDetails.isAccountNonLocked();

        // Then
        assertTrue(isAccountNonLocked);
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        // When
        boolean isCredentialsNonExpired = clientUserDetails.isCredentialsNonExpired();

        // Then
        assertTrue(isCredentialsNonExpired);
    }

    @Test
    void isEnabled_ShouldReturnTrue() {
        // When
        boolean isEnabled = clientUserDetails.isEnabled();

        // Then
        assertTrue(isEnabled);
    }

    @Test
    void getClient_ShouldReturnOriginalClient() {
        // When
        Client client = clientUserDetails.getClient();

        // Then
        assertNotNull(client);
        assertEquals(testClient, client);
        assertEquals(1L, client.getId());
        assertEquals("test@example.com", client.getEmail());
        assertEquals("testuser", client.getUsername());
        assertEquals("encodedPassword", client.getPassword());
        assertEquals(UserRole.CLIENT, client.getUserRole());
    }
}
