package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.cvetochey.model.CartItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByClientId(Long clientId);
    Optional<CartItem> findByClientIdAndProductId(Long clientId, Long productId);
    void deleteByClientId(Long clientId);
}
