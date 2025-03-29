package ru.itmo.cvetochey.controller.impl;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.repository.CatalogRepository;

@RestController
@RequestMapping("cvet-ochey/api/v1/catalog")
public class CatalogController {

    private final CatalogRepository catalogRepository;

    public CatalogController(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @GetMapping("/get-all")
    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Catalog> getCatalogById(@PathVariable Long id) {
        return catalogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Catalog createCatalog(@RequestBody Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Catalog> updateCatalog(@PathVariable Long id, @RequestBody Catalog updated) {
        return catalogRepository.findById(id)
                .map(catalog -> {
                    catalog.setName(updated.getName());
                    catalog.setDescription(updated.getDescription());
                    return ResponseEntity.ok(catalogRepository.save(catalog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Long id) {
        if (!catalogRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        catalogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
