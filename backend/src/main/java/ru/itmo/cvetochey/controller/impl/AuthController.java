package ru.itmo.cvetochey.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.cvetochey.dto.LoginRequest;
import ru.itmo.cvetochey.dto.LoginResponse;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.repository.ClientRepository;
import ru.itmo.cvetochey.security.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cvet-ochey/api/v1")
@CrossOrigin(origins = "*")
public class AuthController {

    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Ищем в БД клиента
        Client client = clientRepository.findByEmail(request.getEmail()).orElse(null);
        if (client == null) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        // Сравним пароль (если хранится в plaintext, просто сравниваем, иначе bcrypt.matches(...))
        if (!client.getPassword().equals(request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        // Генерируем JWT
        String token = jwtUtil.generateToken(client.getEmail());

        // Возвращаем
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
