package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Ordering;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {

}
