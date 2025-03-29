package ru.itmo.cvetochey.dto;

import lombok.Data;

@Data
public class ClientCreateDto {

    private String email;
    private String username;
    private String password;

}
