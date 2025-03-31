package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import ru.itmo.cvetochey.dto.ClientCreateDto;
import ru.itmo.cvetochey.dto.ClientDto;
import ru.itmo.cvetochey.model.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client entity);

    Client toEntity(ClientCreateDto dto);

}
