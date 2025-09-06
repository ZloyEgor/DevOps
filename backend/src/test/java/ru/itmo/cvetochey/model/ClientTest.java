package ru.itmo.cvetochey.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void builder_ShouldCreateClientWithAllFields() {
        // When
        Client client = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("password123")
            .userRole(UserRole.CLIENT)
            .build();

        // Then
        assertNotNull(client);
        assertEquals(1L, client.getId());
        assertEquals("test@example.com", client.getEmail());
        assertEquals("testuser", client.getUsername());
        assertEquals("password123", client.getPassword());
        assertEquals(UserRole.CLIENT, client.getUserRole());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Client client = new Client();

        // When
        client.setId(2L);
        client.setEmail("user@example.com");
        client.setUsername("user123");
        client.setPassword("securePassword");
        client.setUserRole(UserRole.ADMIN);

        // Then
        assertEquals(2L, client.getId());
        assertEquals("user@example.com", client.getEmail());
        assertEquals("user123", client.getUsername());
        assertEquals("securePassword", client.getPassword());
        assertEquals(UserRole.ADMIN, client.getUserRole());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        Client client1 = Client.builder().id(1L).email("test@example.com").build();
        Client client3 = Client.builder().id(2L).email("test@example.com").build();

        // When & Then
        assertEquals(client1, client1); // reflexive
        assertNotEquals(client1, client3); // different IDs
        assertNotEquals(client1, null); // null check
        assertNotEquals(client1, "string"); // different type
    }

    @Test
    void equals_ShouldReturnFalse_WhenClientsHaveDifferentIds() {
        // Given
        Client client1 = Client.builder().id(1L).email("test@example.com").build();
        Client client2 = Client.builder().id(2L).email("test@example.com").build();

        // When & Then
        assertNotEquals(client1, client2);
    }

    @Test
    void toString_ShouldNotBeNull() {
        // Given
        Client client = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .userRole(UserRole.CLIENT)
            .build();

        // When
        String toString = client.toString();

        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }
}
