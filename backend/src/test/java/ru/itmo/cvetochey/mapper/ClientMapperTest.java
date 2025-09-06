package ru.itmo.cvetochey.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.itmo.cvetochey.dto.ClientDto;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;

class ClientMapperTest {

    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        clientMapper = Mappers.getMapper(ClientMapper.class);
    }

    @Test
    void toDto_ShouldMapClientToClientDto() {
        // Given
        Client client = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("password123")
            .userRole(UserRole.CLIENT)
            .build();

        // When
        ClientDto clientDto = clientMapper.toDto(client);

        // Then
        assertNotNull(clientDto);
        assertEquals(1L, clientDto.getId());
        assertEquals("test@example.com", clientDto.getEmail());
        assertEquals("testuser", clientDto.getUsername());
        assertEquals(UserRole.CLIENT, clientDto.getUserRole());
    }

    @Test
    void toDto_ShouldReturnNull_WhenClientIsNull() {
        // When
        ClientDto clientDto = clientMapper.toDto(null);

        // Then
        assertNull(clientDto);
    }

    @Test
    void toDto_ShouldHandleNullFields() {
        // Given
        Client client = Client.builder()
            .id(1L)
            .email(null)
            .username(null)
            .userRole(null)
            .build();

        // When
        ClientDto clientDto = clientMapper.toDto(client);

        // Then
        assertNotNull(clientDto);
        assertEquals(1L, clientDto.getId());
        assertNull(clientDto.getEmail());
        assertNull(clientDto.getUsername());
        assertNull(clientDto.getUserRole());
    }
}
