package ru.itmo.cvetochey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для продукта (название, описание, цена, привязка к каталогу).")
public class ProductDto {

    @Schema(description = "Уникальный идентификатор продукта", example = "42")
    private Long id;

    @Schema(description = "Название продукта", example = "Tulip Bouquet")
    private String name;

    @Schema(description = "Описание продукта", example = "Яркий весенний букет из тюльпанов.")
    private String description;

    @Schema(description = "Цена продукта", example = "15.99")
    private Double price;

    @Schema(description = "Ссылка на изображение / картинку товара", example = "http://example.com/images/tulips.jpg")
    private String pictureUrl;

    @Schema(description = "Идентификатор каталога, к которому относится продукт", example = "101")
    private Long catalogId;

}
