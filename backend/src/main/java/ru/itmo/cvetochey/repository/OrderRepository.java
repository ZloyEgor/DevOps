package ru.itmo.cvetochey.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.Order;
import ru.itmo.cvetochey.model.Product;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClient(Client client);
    
    List<Order> findByClientId(Long clientId);
    
    List<Order> findByProduct(Product product);
    
    List<Order> findByProductId(Long productId);
    
    List<Order> findByTotalPriceBetween(Double minPrice, Double maxPrice);

}
