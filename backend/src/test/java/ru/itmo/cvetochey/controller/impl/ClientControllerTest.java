package ru.itmo.cvetochey.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.dto.ClientDto;
import ru.itmo.cvetochey.mapper.ClientMapper;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

  @Mock private ClientRepository clientRepository;

  @Mock private ClientMapper clientMapper;

  @InjectMocks private ClientController clientController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private Client testClient;
  private ClientDto testClientDto;
  private ClientCreateDto testClientCreateDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    objectMapper = new ObjectMapper();

    testClient =
        Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("password")
            .userRole(UserRole.CLIENT)
            .build();

    testClientDto = new ClientDto();
    testClientDto.setId(1L);
    testClientDto.setEmail("test@example.com");
    testClientDto.setUsername("testuser");
    testClientDto.setUserRole(UserRole.CLIENT);

    testClientCreateDto = new ClientCreateDto();
    testClientCreateDto.setEmail("test@example.com");
    testClientCreateDto.setUsername("testuser");
    testClientCreateDto.setPassword("password");
    testClientCreateDto.setUserRole(UserRole.CLIENT);
  }

  @Test
  void getAll_ShouldReturnAllClients() throws Exception {
    // Given
    List<Client> clients = Arrays.asList(testClient);
    List<ClientDto> clientDtos = Arrays.asList(testClientDto);

    when(clientRepository.findAll()).thenReturn(clients);
    when(clientMapper.toDto(any(Client.class))).thenReturn(testClientDto);

    // When & Then
    mockMvc
        .perform(get("/cvet-ochey/api/v1/clients"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].email").value("test@example.com"));

    verify(clientRepository).findAll();
    verify(clientMapper).toDto(testClient);
  }

  @Test
  void getOne_WhenClientExists_ShouldReturnClient() {
    // Given
    when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    ResponseEntity<ClientDto> response = clientController.getOne(1L);

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(testClientDto, response.getBody());

    verify(clientRepository).findById(1L);
    verify(clientMapper).toDto(testClient);
  }

  @Test
  void getOne_WhenClientNotExists_ShouldReturnNotFound() {
    // Given
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<ClientDto> response = clientController.getOne(1L);

    // Then
    assertEquals(404, response.getStatusCode().value());
    assertNull(response.getBody());

    verify(clientRepository).findById(1L);
    verify(clientMapper, never()).toDto(any());
  }

  @Test
  void create_WithValidData_ShouldCreateClient() {
    // Given
    when(clientRepository.existsByEmail(anyString())).thenReturn(false);
    when(clientRepository.existsByUsername(anyString())).thenReturn(false);
    when(clientMapper.toEntity(testClientCreateDto)).thenReturn(testClient);
    when(clientRepository.save(testClient)).thenReturn(testClient);
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    ResponseEntity<ClientDto> response = clientController.create(testClientCreateDto);

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(testClientDto, response.getBody());

    verify(clientRepository).existsByEmail("test@example.com");
    verify(clientRepository).existsByUsername("testuser");
    verify(clientRepository).save(testClient);
  }

  @Test
  void create_WithExistingEmail_ShouldReturnBadRequest() {
    // Given
    when(clientRepository.existsByEmail(anyString())).thenReturn(true);

    // When
    ResponseEntity<ClientDto> response = clientController.create(testClientCreateDto);

    // Then
    assertEquals(400, response.getStatusCode().value());
    assertNull(response.getBody());

    verify(clientRepository).existsByEmail("test@example.com");
    verify(clientRepository, never()).save(any());
  }

  @Test
  void create_WithExistingUsername_ShouldReturnBadRequest() {
    // Given
    when(clientRepository.existsByEmail(anyString())).thenReturn(false);
    when(clientRepository.existsByUsername(anyString())).thenReturn(true);

    // When
    ResponseEntity<ClientDto> response = clientController.create(testClientCreateDto);

    // Then
    assertEquals(400, response.getStatusCode().value());
    assertNull(response.getBody());

    verify(clientRepository).existsByUsername("testuser");
    verify(clientRepository, never()).save(any());
  }

  @Test
  void update_WithValidData_ShouldUpdateClient() {
    // Given
    when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
    // Create a DTO with different email/username to trigger validation
    ClientCreateDto updateDto = new ClientCreateDto();
    updateDto.setEmail("newemail@example.com");
    updateDto.setUsername("newusername");
    updateDto.setPassword("newpassword");
    updateDto.setUserRole(UserRole.CLIENT);

    when(clientRepository.existsByEmail("newemail@example.com")).thenReturn(false);
    when(clientRepository.existsByUsername("newusername")).thenReturn(false);
    when(clientRepository.save(testClient)).thenReturn(testClient);
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    ResponseEntity<ClientDto> response = clientController.update(1L, updateDto);

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());

    verify(clientRepository).findById(1L);
    verify(clientRepository).existsByEmail("newemail@example.com");
    verify(clientRepository).existsByUsername("newusername");
    verify(clientRepository).save(testClient);
  }

  @Test
  void update_WhenClientNotExists_ShouldReturnNotFound() {
    // Given
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<ClientDto> response = clientController.update(1L, testClientCreateDto);

    // Then
    assertEquals(404, response.getStatusCode().value());

    verify(clientRepository).findById(1L);
    verify(clientRepository, never()).save(any());
  }

  @Test
  void delete_WhenClientExists_ShouldDeleteClient() {
    // Given
    when(clientRepository.existsById(1L)).thenReturn(true);

    // When
    ResponseEntity<Void> response = clientController.delete(1L);

    // Then
    assertEquals(204, response.getStatusCode().value());

    verify(clientRepository).existsById(1L);
    verify(clientRepository).deleteById(1L);
  }

  @Test
  void delete_WhenClientNotExists_ShouldReturnNotFound() {
    // Given
    when(clientRepository.existsById(1L)).thenReturn(false);

    // When
    ResponseEntity<Void> response = clientController.delete(1L);

    // Then
    assertEquals(404, response.getStatusCode().value());

    verify(clientRepository).existsById(1L);
    verify(clientRepository, never()).deleteById(anyLong());
  }

  @Test
  void getByEmail_WhenClientExists_ShouldReturnClient() {
    // Given
    when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testClient));
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    ResponseEntity<ClientDto> response = clientController.getByEmail("test@example.com");

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(testClientDto, response.getBody());

    verify(clientRepository).findByEmail("test@example.com");
  }

  @Test
  void getByUsername_WhenClientExists_ShouldReturnClient() {
    // Given
    when(clientRepository.findByUsername("testuser")).thenReturn(Optional.of(testClient));
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    ResponseEntity<ClientDto> response = clientController.getByUsername("testuser");

    // Then
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(testClientDto, response.getBody());

    verify(clientRepository).findByUsername("testuser");
  }

  @Test
  void getByRole_ShouldReturnClientsWithRole() {
    // Given
    List<Client> clients = Arrays.asList(testClient);
    when(clientRepository.findByUserRole(UserRole.CLIENT)).thenReturn(clients);
    when(clientMapper.toDto(testClient)).thenReturn(testClientDto);

    // When
    List<ClientDto> result = clientController.getByRole(UserRole.CLIENT);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testClientDto, result.get(0));

    verify(clientRepository).findByUserRole(UserRole.CLIENT);
  }
}
