package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
