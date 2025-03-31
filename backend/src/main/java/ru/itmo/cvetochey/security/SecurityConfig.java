package ru.itmo.cvetochey.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.itmo.cvetochey.repository.ClientRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ClientRepository clientRepository;

    @Bean
    public SecurityFilterChain filterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http
    ) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil, clientRepository);
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/cvet-ochey/api/v1/login",
                                "/cvet-ochey/api/v1/catalog/get-all"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
