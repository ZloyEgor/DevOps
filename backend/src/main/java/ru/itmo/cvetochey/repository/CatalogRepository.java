package ru.itmo.cvetochey.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Catalog;
import ru.itmo.cvetochey.model.CatalogType;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

    List<Catalog> findByCatalogType(CatalogType catalogType);

}
