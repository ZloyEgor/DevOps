package ru.itmo.cvetochey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import ru.itmo.cvetochey.model.CatalogType;

@Data
@Schema(description = "DTO для каталога (название, описание, тип, список продуктов).")
public class CatalogDto {

    @Schema(description = "Уникальный идентификатор каталога", example = "101")
    private Long id;

    @Schema(description = "Название каталога", example = "Spring Flowers")
    private String name;

    @Schema(description = "Описание каталога", example = "Различные весенние цветы, букеты и композиции.")
    private String description;

    @Schema(description = "Тип каталога", example = "FLOWERS")
    private CatalogType catalogType;

    @Schema(description = "Список продуктов, относящихся к этому каталогу",
            implementation = ProductDto.class
    )
    private List<ProductDto> productDtos;
}
