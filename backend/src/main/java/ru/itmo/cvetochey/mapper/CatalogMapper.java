package ru.itmo.cvetochey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.cvetochey.dto.CatalogDto;
import ru.itmo.cvetochey.model.Catalog;

@Mapper(componentModel = "spring")
public abstract class CatalogMapper {

    @Mapping(
            target = "productIds",
            expression = "java(catalog.getProducts() == null"
                    + " ? null"
                    + " : catalog.getProducts().stream().map(Product::getId)"
                    + ".collect(Collectors.toList()))"
    )
    public abstract CatalogDto toDto(Catalog catalog);

    @Mapping(target = "products", ignore = true)
    public abstract Catalog toEntity(CatalogDto dto);

}
