package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.cvetochey.dto.CartItemDto;
import ru.itmo.cvetochey.model.CartItem;

@Mapper(
    componentModel = "spring",
    uses = {ProductMapper.class})
public interface CartItemMapper {

  @Mapping(source = "client.id", target = "clientId")
  @Mapping(
      target = "totalPrice",
      expression = "java(cartItem.getProduct().getPrice() * cartItem.getQuantity())")
  CartItemDto toDto(CartItem cartItem);

  @Mapping(source = "clientId", target = "client.id")
  CartItem toEntity(CartItemDto cartItemDto);
}
