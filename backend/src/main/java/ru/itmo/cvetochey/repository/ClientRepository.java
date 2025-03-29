package ru.itmo.cvetochey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
