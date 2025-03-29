package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
