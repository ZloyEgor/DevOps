package ru.itmo.cvetochey.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itmo.cvetochey.dto.CatalogDto;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.Product;

@Mapper(componentModel = "spring")
public abstract class CatalogMapper {

    @Autowired
    protected ProductMapper productMapper;

    @Mapping(target = "productDtos", expression = "java(mapProductsToDtos(catalog.getProducts()))")
    public abstract CatalogDto toDto(Catalog catalog);

    @Mapping(target = "products", ignore = true)
    public abstract Catalog toEntity(CatalogDto dto);

    List<ProductDto> mapProductsToDtos(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

}
