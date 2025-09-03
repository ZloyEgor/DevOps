package ru.itmo.cvetochey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.cvetochey.dto.AuthRequestDto;
import ru.itmo.cvetochey.dto.AuthResponseDto;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.dto.ClientDto;
import ru.itmo.cvetochey.mapper.ClientMapper;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;
import ru.itmo.cvetochey.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ClientMapper clientMapper;

    public AuthResponseDto register(ClientCreateDto request) {
        // Check if user already exists
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }
        if (clientRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }

        var client = Client.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(request.getUserRole() != null ? request.getUserRole() : UserRole.CLIENT)
                .build();

        var savedClient = clientRepository.save(client);
        var clientDetails = new ClientUserDetails(savedClient);
        var jwtToken = jwtService.generateToken(clientDetails);
        var refreshToken = jwtService.generateRefreshToken(clientDetails);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .user(clientMapper.toDto(savedClient))
                .build();
    }

    public AuthResponseDto authenticate(AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var client = clientRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var clientDetails = new ClientUserDetails(client);
        var jwtToken = jwtService.generateToken(clientDetails);
        var refreshToken = jwtService.generateRefreshToken(clientDetails);

        return AuthResponseDto.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .user(clientMapper.toDto(client))
                .build();
    }
}
