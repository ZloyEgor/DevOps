package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.cvetochey.dto.OrderDto;
import ru.itmo.cvetochey.model.Order;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "productId", source = "product.id")
    public abstract OrderDto toDto(Order entity);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "product", ignore = true)
    public abstract Order toEntity(OrderDto dto);
}
