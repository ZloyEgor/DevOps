package ru.itmo.cvetochey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
  private String token;
  private String refreshToken;
  private ClientDto user;

  @Builder.Default private String tokenType = "Bearer";
}
