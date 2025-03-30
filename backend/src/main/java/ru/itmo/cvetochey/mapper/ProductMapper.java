package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.model.Product;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Mapping(target = "catalogId", source = "catalog.id")
    public abstract ProductDto toDto(Product entity);

    @Mapping(target = "catalog", ignore = true)
    public abstract Product toEntity(ProductDto dto);

}
