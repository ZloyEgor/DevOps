package ru.itmo.cvetochey.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.itmo.cvetochey.model.UserRole;

class ClientDtoTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        ClientDto clientDto = new ClientDto();

        // When
        clientDto.setId(1L);
        clientDto.setEmail("test@example.com");
        clientDto.setUsername("testuser");
        clientDto.setUserRole(UserRole.CLIENT);

        // Then
        assertEquals(1L, clientDto.getId());
        assertEquals("test@example.com", clientDto.getEmail());
        assertEquals("testuser", clientDto.getUsername());
        assertEquals(UserRole.CLIENT, clientDto.getUserRole());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        ClientDto dto1 = new ClientDto();
        dto1.setId(1L);
        dto1.setEmail("test@example.com");

        ClientDto dto2 = new ClientDto();
        dto2.setId(1L);
        dto2.setEmail("test@example.com");

        ClientDto dto3 = new ClientDto();
        dto3.setId(2L);
        dto3.setEmail("test@example.com");

        // When & Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldNotBeNull() {
        // Given
        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setEmail("test@example.com");

        // When
        String toString = clientDto.toString();

        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }
}
