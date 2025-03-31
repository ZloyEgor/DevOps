package ru.itmo.cvetochey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.itmo.cvetochey.model.UserRole;

@Data
@Schema(description = "DTO для создания/редактирования клиента.")
public class ClientCreateDto {

    @Schema(description = "Идентификатор клиента (может быть null при создании)", example = "1")
    private Long id;

    @Schema(description = "Email-адрес клиента", example = "bob@example.com")
    private String email;

    @Schema(description = "Логин (уникальное имя пользователя)", example = "Bob")
    private String username;

    @Schema(description = "Пароль (входные данные, не возвращаются наружу)", example = "P@ssw0rd!")
    private String password;

    @Schema(description = "Роль пользователя: CLIENT или ADMIN", example = "CLIENT")
    private UserRole userRole;

}
