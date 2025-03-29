package ru.itmo.cvetochey.dto;

import lombok.Data;
import ru.itmo.cvetochey.model.UserRole;

@Data
public class ClientDto {

    private Long id;
    private String email;
    private String username;
    private UserRole userRole;

}