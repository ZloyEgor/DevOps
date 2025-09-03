package ru.itmo.cvetochey.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCatalog(Catalog catalog);
    
    List<Product> findByCatalogId(Long catalogId);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

}
