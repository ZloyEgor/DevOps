package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "catalog", ignore = true)
    Product toEntity(ProductDto dto);

    @Mapping(target = "catalogId", source = "catalog.id")
    ProductDto toDto(Product entity);
}
