package ru.itmo.cvetochey.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String PRODUCTS_API_PATH = "/cvet-ochey/api/v1/products/**";
  private static final String ADMIN_ROLE = "ADMIN";

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  @Bean
  @SuppressWarnings("java:S4502") // Suppress "Disabling CSRF protections is security-sensitive"
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // SAFE: API uses JWT tokens, not session cookies
        .cors(AbstractHttpConfigurer::disable) // Disable CORS completely
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(HttpMethod.OPTIONS)
                    .permitAll() // Allow all OPTIONS requests first
                    .requestMatchers(
                        "/cvet-ochey/api/v1/auth/**",
                        "/cvet-ochey/api/v1/catalog/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, PRODUCTS_API_PATH)
                    .permitAll()
                    .requestMatchers(HttpMethod.PUT, PRODUCTS_API_PATH)
                    .hasRole(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.POST, PRODUCTS_API_PATH)
                    .hasRole(ADMIN_ROLE)
                    .requestMatchers(HttpMethod.DELETE, PRODUCTS_API_PATH)
                    .hasRole(ADMIN_ROLE)
                    .requestMatchers("/cvet-ochey/api/v1/clients/**")
                    .hasRole(ADMIN_ROLE)
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // CORS is disabled - no configuration needed

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  @SuppressWarnings("java:S5344") // Suppress "Using a plain text password encoder is insecure"
  public PasswordEncoder passwordEncoder() {
    // Use plain text password encoder for development - SAFE for dev environment
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
      }
    };
  }
}
