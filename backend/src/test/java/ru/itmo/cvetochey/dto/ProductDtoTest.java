package ru.itmo.cvetochey.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductDtoTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        ProductDto productDto = new ProductDto();

        // When
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(29.99);
        productDto.setPictureUrl("http://example.com/image.jpg");
        productDto.setCatalogId(100L);

        // Then
        assertEquals(1L, productDto.getId());
        assertEquals("Test Product", productDto.getName());
        assertEquals("Test Description", productDto.getDescription());
        assertEquals(29.99, productDto.getPrice());
        assertEquals("http://example.com/image.jpg", productDto.getPictureUrl());
        assertEquals(100L, productDto.getCatalogId());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        ProductDto dto1 = new ProductDto();
        dto1.setId(1L);
        dto1.setName("Product");

        ProductDto dto2 = new ProductDto();
        dto2.setId(1L);
        dto2.setName("Product");

        ProductDto dto3 = new ProductDto();
        dto3.setId(2L);
        dto3.setName("Product");

        // When & Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldNotBeNull() {
        // Given
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");

        // When
        String toString = productDto.toString();

        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }
}
