package ru.itmo.cvetochey.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.mapper.ProductMapper;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.CatalogType;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.CatalogRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CatalogRepository catalogRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private Product testProduct;
    private ProductDto testProductDto;
    private Catalog testCatalog;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testCatalog = Catalog.builder()
                .id(1L)
                .name("Spring Flowers")
                .description("Beautiful spring flower collection")
                .catalogType(CatalogType.FLOWERS)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Rose Bouquet")
                .description("Beautiful red roses")
                .price(25.99)
                .pictureUrl("http://example.com/roses.jpg")
                .catalog(testCatalog)
                .build();

        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setName("Rose Bouquet");
        testProductDto.setDescription("Beautiful red roses");
        testProductDto.setPrice(25.99);
        testProductDto.setPictureUrl("http://example.com/roses.jpg");
        testProductDto.setCatalogId(1L);
    }

    @Test
    void getAll_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDto(any(Product.class))).thenReturn(testProductDto);

        // When
        List<ProductDto> result = productController.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProductDto, result.get(0));

        verify(productRepository).findAll();
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void getOne_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        ResponseEntity<ProductDto> response = productController.getOne(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testProductDto, response.getBody());

        verify(productRepository).findById(1L);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void getOne_WhenProductNotExists_ShouldReturnNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductDto> response = productController.getOne(1L);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(productRepository).findById(1L);
        verify(productMapper, never()).toDto(any());
    }

    @Test
    void create_WithValidData_ShouldCreateProduct() {
        // Given
        when(productMapper.toEntity(testProductDto)).thenReturn(testProduct);
        when(catalogRepository.findById(1L)).thenReturn(Optional.of(testCatalog));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        ResponseEntity<ProductDto> response = productController.create(testProductDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testProductDto, response.getBody());

        verify(productMapper).toEntity(testProductDto);
        verify(catalogRepository).findById(1L);
        verify(productRepository).save(testProduct);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void create_WithInvalidCatalogId_ShouldReturnBadRequest() {
        // Given
        when(productMapper.toEntity(testProductDto)).thenReturn(testProduct);
        when(catalogRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductDto> response = productController.create(testProductDto);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(catalogRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_WithNullCatalogId_ShouldCreateProduct() {
        // Given
        testProductDto.setCatalogId(null);
        when(productMapper.toEntity(testProductDto)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        ResponseEntity<ProductDto> response = productController.create(testProductDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(productRepository).save(testProduct);
        verify(catalogRepository, never()).findById(anyLong());
    }

    @Test
    void update_WhenProductExists_ShouldUpdateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(catalogRepository.findById(1L)).thenReturn(Optional.of(testCatalog));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        ResponseEntity<ProductDto> response = productController.update(1L, testProductDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(productRepository).findById(1L);
        verify(catalogRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void update_WhenProductNotExists_ShouldReturnNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ProductDto> response = productController.update(1L, testProductDto);

        // Then
        assertEquals(404, response.getStatusCodeValue());

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_WhenProductExists_ShouldDeleteProduct() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> response = productController.delete(1L);

        // Then
        assertEquals(204, response.getStatusCodeValue());

        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void delete_WhenProductNotExists_ShouldReturnNotFound() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(false);

        // When
        ResponseEntity<Void> response = productController.delete(1L);

        // Then
        assertEquals(404, response.getStatusCodeValue());

        verify(productRepository).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void getByCatalogId_ShouldReturnProductsByCatalog() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCatalogId(1L)).thenReturn(products);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        List<ProductDto> result = productController.getByCatalogId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProductDto, result.get(0));

        verify(productRepository).findByCatalogId(1L);
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void searchByName_ShouldReturnMatchingProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase("rose")).thenReturn(products);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        List<ProductDto> result = productController.searchByName("rose");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProductDto, result.get(0));

        verify(productRepository).findByNameContainingIgnoreCase("rose");
        verify(productMapper).toDto(testProduct);
    }

    @Test
    void getByPriceRange_ShouldReturnProductsInRange() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByPriceBetween(20.0, 30.0)).thenReturn(products);
        when(productMapper.toDto(testProduct)).thenReturn(testProductDto);

        // When
        List<ProductDto> result = productController.getByPriceRange(20.0, 30.0);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProductDto, result.get(0));

        verify(productRepository).findByPriceBetween(20.0, 30.0);
        verify(productMapper).toDto(testProduct);
    }
}
