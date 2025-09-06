package ru.itmo.cvetochey.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.CatalogType;
import ru.itmo.cvetochey.model.Product;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void toDto_ShouldMapProductToProductDto() {
        // Given
        Catalog catalog = Catalog.builder()
            .id(100L)
            .name("Test Catalog")
            .catalogType(CatalogType.FLOWERS)
            .build();

        Product product = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .pictureUrl("http://example.com/image.jpg")
            .catalog(catalog)
            .build();

        // When
        ProductDto productDto = productMapper.toDto(product);

        // Then
        assertNotNull(productDto);
        assertEquals(1L, productDto.getId());
        assertEquals("Test Product", productDto.getName());
        assertEquals("Test Description", productDto.getDescription());
        assertEquals(29.99, productDto.getPrice());
        assertEquals("http://example.com/image.jpg", productDto.getPictureUrl());
        assertEquals(100L, productDto.getCatalogId());
    }

    @Test
    void toDto_ShouldReturnNull_WhenProductIsNull() {
        // When
        ProductDto productDto = productMapper.toDto(null);

        // Then
        assertNull(productDto);
    }

    @Test
    void toDto_ShouldHandleNullCatalog() {
        // Given
        Product product = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .pictureUrl("http://example.com/image.jpg")
            .catalog(null)
            .build();

        // When
        ProductDto productDto = productMapper.toDto(product);

        // Then
        assertNotNull(productDto);
        assertEquals(1L, productDto.getId());
        assertEquals("Test Product", productDto.getName());
        assertEquals("Test Description", productDto.getDescription());
        assertEquals(29.99, productDto.getPrice());
        assertEquals("http://example.com/image.jpg", productDto.getPictureUrl());
        assertNull(productDto.getCatalogId());
    }
}
