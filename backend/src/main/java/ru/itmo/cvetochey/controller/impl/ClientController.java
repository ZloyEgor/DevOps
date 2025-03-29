package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.repository.ClientRepository;

@RestController
@RequestMapping("cvet-ochey/api/v1/client")
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/get-all")
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Client> getUserById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public Client createClient(@RequestBody Client user) {
        return clientRepository.save(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Client> updateUser(@PathVariable Long id, @RequestBody Client updated) {
        return clientRepository.findById(id)
                .map(user -> {
                    user.setUsername(updated.getUsername());
                    user.setEmail(updated.getEmail());
                    return ResponseEntity.ok(clientRepository.save(user));
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!clientRepository.existsById(id)) {
            return ResponseEntity.noContent().build();
        }
        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
