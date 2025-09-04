package ru.itmo.cvetochey.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.AuthRequestDto;
import ru.itmo.cvetochey.dto.AuthResponseDto;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.service.AuthService;

@RestController
@RequestMapping("/cvet-ochey/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody ClientCreateDto request) {
    try {
      return ResponseEntity.ok(authService.register(request));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> authenticate(@RequestBody AuthRequestDto request) {
    try {
      return ResponseEntity.ok(authService.authenticate(request));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // In a stateless JWT setup, logout is typically handled client-side
    // by simply discarding the token. Here we just return success.
    return ResponseEntity.ok().build();
  }
}
