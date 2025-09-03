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
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.dto.CatalogDto;
import ru.itmo.cvetochey.mapper.CatalogMapper;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.CatalogType;
import ru.itmo.cvetochey.repository.CatalogRepository;

@RestController
@RequestMapping("/cvet-ochey/api/v1/catalog")
@CrossOrigin(origins = "*")
public class CatalogController {

    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    public CatalogController(CatalogRepository catalogRepository, CatalogMapper catalogMapper) {
        this.catalogRepository = catalogRepository;
        this.catalogMapper = catalogMapper;
    }

    @GetMapping
    public List<CatalogDto> getAll() {
        return catalogRepository.findAll().stream()
                .map(catalogMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogDto> getOne(@PathVariable Long id) {
        return catalogRepository.findById(id)
                .map(catalogMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CatalogDto> create(@RequestBody CatalogDto dto) {
        Catalog catalog = catalogMapper.toEntity(dto);
        Catalog saved = catalogRepository.save(catalog);
        return ResponseEntity.ok(catalogMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogDto> update(@PathVariable Long id, @RequestBody CatalogDto dto) {
        return catalogRepository.findById(id)
                .map(entity -> {
                    entity.setName(dto.getName());
                    entity.setDescription(dto.getDescription());
                    entity.setCatalogType(dto.getCatalogType());
                    catalogRepository.save(entity);
                    return catalogMapper.toDto(entity);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!catalogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        catalogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byType/{type}")
    public List<CatalogDto> getByType(@PathVariable CatalogType type) {
        return catalogRepository.findByCatalogType(type).stream()
                .map(catalogMapper::toDto)
                .collect(Collectors.toList());
    }

}
