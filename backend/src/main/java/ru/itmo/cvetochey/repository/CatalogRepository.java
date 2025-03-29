package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Catalog;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {

}
