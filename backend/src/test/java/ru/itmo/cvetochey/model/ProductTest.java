package ru.itmo.cvetochey.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void builder_ShouldCreateProductWithAllFields() {
        // Given
        Catalog catalog = Catalog.builder()
            .id(1L)
            .name("Test Catalog")
            .catalogType(CatalogType.FLOWERS)
            .build();

        // When
        Product product = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .pictureUrl("http://example.com/image.jpg")
            .catalog(catalog)
            .build();

        // Then
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(29.99, product.getPrice());
        assertEquals("http://example.com/image.jpg", product.getPictureUrl());
        assertEquals(catalog, product.getCatalog());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Product product = new Product();
        Catalog catalog = new Catalog();

        // When
        product.setId(2L);
        product.setName("Updated Product");
        product.setDescription("Updated Description");
        product.setPrice(49.99);
        product.setPictureUrl("http://example.com/updated.jpg");
        product.setCatalog(catalog);

        // Then
        assertEquals(2L, product.getId());
        assertEquals("Updated Product", product.getName());
        assertEquals("Updated Description", product.getDescription());
        assertEquals(49.99, product.getPrice());
        assertEquals("http://example.com/updated.jpg", product.getPictureUrl());
        assertEquals(catalog, product.getCatalog());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // Given
        Product product1 = Product.builder().id(1L).name("Product 1").build();
        Product product3 = Product.builder().id(2L).name("Product 1").build();

        // When & Then
        assertEquals(product1, product1); // reflexive
        assertNotEquals(product1, product3); // different IDs
        assertNotEquals(product1, null); // null check
        assertNotEquals(product1, "string"); // different type
    }

    @Test
    void equals_ShouldReturnFalse_WhenProductsHaveDifferentIds() {
        // Given
        Product product1 = Product.builder().id(1L).name("Product").build();
        Product product2 = Product.builder().id(2L).name("Product").build();

        // When & Then
        assertNotEquals(product1, product2);
    }

    @Test
    void toString_ShouldNotBeNull() {
        // Given
        Product product = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .price(29.99)
            .build();

        // When
        String toString = product.toString();

        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }
}
