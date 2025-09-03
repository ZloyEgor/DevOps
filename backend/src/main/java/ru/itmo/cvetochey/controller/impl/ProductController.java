package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.mapper.ProductMapper;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.CatalogRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

@RestController
@RequestMapping("/cvet-ochey/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;
    private final CatalogRepository catalogRepository;
    private final ProductMapper productMapper;

    public ProductController(ProductRepository productRepository,
                           CatalogRepository catalogRepository,
                           ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
        this.productMapper = productMapper;
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getOne(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        Product entity = productMapper.toEntity(dto);
        
        // Validate catalog if provided
        if (dto.getCatalogId() != null) {
            Catalog catalog = catalogRepository.findById(dto.getCatalogId()).orElse(null);
            if (catalog == null) {
                return ResponseEntity.badRequest().build();
            }
            entity.setCatalog(catalog);
        }
        
        Product saved = productRepository.save(entity);
        return ResponseEntity.ok(productMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        return productRepository.findById(id)
                .map(entity -> {
                    entity.setName(dto.getName());
                    entity.setDescription(dto.getDescription());
                    entity.setPrice(dto.getPrice());
                    entity.setPictureUrl(dto.getPictureUrl());
                    if (dto.getCatalogId() != null) {
                        Catalog cat = catalogRepository.findById(dto.getCatalogId()).orElse(null);
                        entity.setCatalog(cat);
                    } else {
                        entity.setCatalog(null);
                    }
                    productRepository.save(entity);
                    return productMapper.toDto(entity);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/catalog/{catalogId}")
    public List<ProductDto> getByCatalogId(@PathVariable Long catalogId) {
        return productRepository.findByCatalogId(catalogId).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ProductDto> searchByName(@RequestParam String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/price-range")
    public List<ProductDto> getByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

}
