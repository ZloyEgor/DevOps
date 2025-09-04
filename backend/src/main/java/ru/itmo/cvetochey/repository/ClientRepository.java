package ru.itmo.cvetochey.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;

public interface ClientRepository extends JpaRepository<Client, Long> {

  Optional<Client> findByEmail(String email);

  Optional<Client> findByUsername(String username);

  List<Client> findByUserRole(UserRole userRole);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
