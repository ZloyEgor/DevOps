package ru.itmo.cvetochey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.cvetochey.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientUserDetailsService implements UserDetailsService {

  private final ClientRepository clientRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var client =
        clientRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    return new ClientUserDetails(client);
  }
}
