package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import ru.itmo.cvetochey.repository.ClientRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cvet-ochey/api/v1/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @GetMapping
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getOne(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ClientDto create(@RequestBody ClientCreateDto dto) {
        Client entity = clientMapper.toEntity(dto);
        Client saved = clientRepository.save(entity);
        return clientMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientCreateDto dto) {
        return clientRepository.findById(id)
                .map(entity -> {
                    entity.setEmail(dto.getEmail());
                    entity.setUsername(dto.getUsername());
                    if (dto.getPassword() != null) {
                        entity.setPassword(dto.getPassword());
                    }
                    entity.setUserRole(dto.getUserRole());
                    clientRepository.save(entity);
                    return clientMapper.toDto(entity);
                })
                .map(ResponseEntity::ok)
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

}
