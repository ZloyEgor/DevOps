package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.dto.ClientDto;
import ru.itmo.cvetochey.mapper.ClientMapper;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.repository.ClientRepository;

@RestController
@RequestMapping("/cvet-ochey/api/v1/clients")
@CrossOrigin(origins = "*")
public class ClientController {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  public ClientController(ClientRepository clientRepository, ClientMapper clientMapper) {
    this.clientRepository = clientRepository;
    this.clientMapper = clientMapper;
  }

  @GetMapping
  public List<ClientDto> getAll() {
    return clientRepository.findAll().stream()
        .map(clientMapper::toDto)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientDto> getOne(@PathVariable Long id) {
    return clientRepository
        .findById(id)
        .map(clientMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<ClientDto> create(@RequestBody ClientCreateDto dto) {
    // Check if email already exists
    if (dto.getEmail() != null && clientRepository.existsByEmail(dto.getEmail())) {
      return ResponseEntity.badRequest().build();
    }
    // Check if username already exists
    if (dto.getUsername() != null && clientRepository.existsByUsername(dto.getUsername())) {
      return ResponseEntity.badRequest().build();
    }

    Client entity = clientMapper.toEntity(dto);
    Client saved = clientRepository.save(entity);
    return ResponseEntity.ok(clientMapper.toDto(saved));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientCreateDto dto) {
    return clientRepository
        .findById(id)
        .map(
            entity -> {
              // Check if email is being changed and if new email already exists
              if (dto.getEmail() != null
                  && !dto.getEmail().equals(entity.getEmail())
                  && clientRepository.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest().<ClientDto>build();
              }
              // Check if username is being changed and if new username already exists
              if (dto.getUsername() != null
                  && !dto.getUsername().equals(entity.getUsername())
                  && clientRepository.existsByUsername(dto.getUsername())) {
                return ResponseEntity.badRequest().<ClientDto>build();
              }

              entity.setEmail(dto.getEmail());
              entity.setUsername(dto.getUsername());
              if (dto.getPassword() != null) {
                entity.setPassword(dto.getPassword());
              }
              entity.setUserRole(dto.getUserRole());
              clientRepository.save(entity);
              return ResponseEntity.ok(clientMapper.toDto(entity));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!clientRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    clientRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<ClientDto> getByEmail(@PathVariable String email) {
    return clientRepository
        .findByEmail(email)
        .map(clientMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<ClientDto> getByUsername(@PathVariable String username) {
    return clientRepository
        .findByUsername(username)
        .map(clientMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/role/{role}")
  public List<ClientDto> getByRole(@PathVariable UserRole role) {
    return clientRepository.findByUserRole(role).stream()
        .map(clientMapper::toDto)
        .collect(Collectors.toList());
  }
}
