package ru.itmo.cvetochey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.itmo.cvetochey.model.UserRole;

@Data
@Schema(description = "DTO для клиента (содержит email, username, роль).")
public class ClientDto {

    @Schema(description = "Уникальный идентификатор клиента", example = "1")
    private Long id;

    @Schema(description = "Email-адрес клиента", example = "alice@example.com")
    private String email;

    @Schema(description = "Логин клиента", example = "Alice")
    private String username;

    @Schema(description = "Роль пользователя: CLIENT или ADMIN", example = "CLIENT")
    private UserRole userRole;

}
