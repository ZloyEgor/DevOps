package ru.itmo.cvetochey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для заказа (итоговая цена, ссылка на клиента и товар).")
public class OrderDto {

    @Schema(description = "Уникальный идентификатор заказа", example = "77")
    private Long id;

    @Schema(description = "Итоговая цена заказа", example = "49.99")
    private Double totalPrice;

    @Schema(description = "Идентификатор клиента, оформившего заказ", example = "1")
    private Long clientId;

    @Schema(description = "Идентификатор продукта, который заказали", example = "42")
    private Long productId;
}
