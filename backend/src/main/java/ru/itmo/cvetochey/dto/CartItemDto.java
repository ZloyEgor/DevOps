package ru.itmo.cvetochey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private Long clientId;
    private ProductDto product;
    private Integer quantity;
    private Double totalPrice;
}
