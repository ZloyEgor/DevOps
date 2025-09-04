package ru.itmo.cvetochey.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.cvetochey.dto.CatalogDto;
import ru.itmo.cvetochey.mapper.CatalogMapper;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.CatalogType;
import ru.itmo.cvetochey.repository.CatalogRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private CatalogRepository catalogRepository;

    @Mock
    private CatalogMapper catalogMapper;

    @InjectMocks
    private CatalogController catalogController;

    private ObjectMapper objectMapper;
    private Catalog testCatalog;
    private CatalogDto testCatalogDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testCatalog = Catalog.builder()
                .id(1L)
                .name("Spring Flowers")
                .description("Beautiful spring flower collection")
                .catalogType(CatalogType.FLOWERS)
                .build();

        testCatalogDto = new CatalogDto();
        testCatalogDto.setId(1L);
        testCatalogDto.setName("Spring Flowers");
        testCatalogDto.setDescription("Beautiful spring flower collection");
        testCatalogDto.setCatalogType(CatalogType.FLOWERS);
    }

    @Test
    void getAll_ShouldReturnAllCatalogs() {
        // Given
        List<Catalog> catalogs = Arrays.asList(testCatalog);
        when(catalogRepository.findAll()).thenReturn(catalogs);
        when(catalogMapper.toDto(any(Catalog.class))).thenReturn(testCatalogDto);

        // When
        List<CatalogDto> result = catalogController.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCatalogDto, result.get(0));

        verify(catalogRepository).findAll();
        verify(catalogMapper).toDto(testCatalog);
    }

    @Test
    void getOne_WhenCatalogExists_ShouldReturnCatalog() {
        // Given
        when(catalogRepository.findById(1L)).thenReturn(Optional.of(testCatalog));
        when(catalogMapper.toDto(testCatalog)).thenReturn(testCatalogDto);

        // When
        ResponseEntity<CatalogDto> response = catalogController.getOne(1L);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testCatalogDto, response.getBody());

        verify(catalogRepository).findById(1L);
        verify(catalogMapper).toDto(testCatalog);
    }

    @Test
    void getOne_WhenCatalogNotExists_ShouldReturnNotFound() {
        // Given
        when(catalogRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<CatalogDto> response = catalogController.getOne(1L);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(catalogRepository).findById(1L);
        verify(catalogMapper, never()).toDto(any());
    }

    @Test
    void create_WithValidData_ShouldCreateCatalog() {
        // Given
        when(catalogMapper.toEntity(testCatalogDto)).thenReturn(testCatalog);
        when(catalogRepository.save(testCatalog)).thenReturn(testCatalog);
        when(catalogMapper.toDto(testCatalog)).thenReturn(testCatalogDto);

        // When
        ResponseEntity<CatalogDto> response = catalogController.create(testCatalogDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testCatalogDto, response.getBody());

        verify(catalogMapper).toEntity(testCatalogDto);
        verify(catalogRepository).save(testCatalog);
        verify(catalogMapper).toDto(testCatalog);
    }

    @Test
    void update_WhenCatalogExists_ShouldUpdateCatalog() {
        // Given
        when(catalogRepository.findById(1L)).thenReturn(Optional.of(testCatalog));
        when(catalogRepository.save(testCatalog)).thenReturn(testCatalog);
        when(catalogMapper.toDto(testCatalog)).thenReturn(testCatalogDto);

        // When
        ResponseEntity<CatalogDto> response = catalogController.update(1L, testCatalogDto);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(catalogRepository).findById(1L);
        verify(catalogRepository).save(testCatalog);
        verify(catalogMapper).toDto(testCatalog);
    }

    @Test
    void update_WhenCatalogNotExists_ShouldReturnNotFound() {
        // Given
        when(catalogRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<CatalogDto> response = catalogController.update(1L, testCatalogDto);

        // Then
        assertEquals(404, response.getStatusCodeValue());

        verify(catalogRepository).findById(1L);
        verify(catalogRepository, never()).save(any());
    }

    @Test
    void delete_WhenCatalogExists_ShouldDeleteCatalog() {
        // Given
        when(catalogRepository.existsById(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> response = catalogController.delete(1L);

        // Then
        assertEquals(204, response.getStatusCodeValue());

        verify(catalogRepository).existsById(1L);
        verify(catalogRepository).deleteById(1L);
    }

    @Test
    void delete_WhenCatalogNotExists_ShouldReturnNotFound() {
        // Given
        when(catalogRepository.existsById(1L)).thenReturn(false);

        // When
        ResponseEntity<Void> response = catalogController.delete(1L);

        // Then
        assertEquals(404, response.getStatusCodeValue());

        verify(catalogRepository).existsById(1L);
        verify(catalogRepository, never()).deleteById(anyLong());
    }

    @Test
    void getByType_ShouldReturnCatalogsByType() {
        // Given
        List<Catalog> catalogs = Arrays.asList(testCatalog);
        when(catalogRepository.findByCatalogType(CatalogType.FLOWERS)).thenReturn(catalogs);
        when(catalogMapper.toDto(testCatalog)).thenReturn(testCatalogDto);

        // When
        List<CatalogDto> result = catalogController.getByType(CatalogType.FLOWERS);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCatalogDto, result.get(0));

        verify(catalogRepository).findByCatalogType(CatalogType.FLOWERS);
        verify(catalogMapper).toDto(testCatalog);
    }

    @Test
    void getByType_WhenNoCatalogsFound_ShouldReturnEmptyList() {
        // Given
        when(catalogRepository.findByCatalogType(CatalogType.FLOWERS)).thenReturn(Arrays.asList());

        // When
        List<CatalogDto> result = catalogController.getByType(CatalogType.FLOWERS);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(catalogRepository).findByCatalogType(CatalogType.FLOWERS);
        verify(catalogMapper, never()).toDto(any());
    }
}
