package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.mapper.ProductMapper;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.CatalogRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cvet-ochey/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepository;
    private final CatalogRepository catalogRepository;
    private final ProductMapper productMapper;

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
    public ProductDto create(@RequestBody ProductDto dto) {
        Product entity = productMapper.toEntity(dto);
        if (dto.getCatalogId() != null) {
            catalogRepository.findById(dto.getCatalogId()).ifPresent(entity::setCatalog);
        }
        Product saved = productRepository.save(entity);
        return productMapper.toDto(saved);
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

}
