package ru.itmo.cvetochey.service;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.UserRole;

class JwtServiceTest {

    private JwtService jwtService;
    private Client testClient;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 hours

        testClient = Client.builder()
            .id(1L)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.CLIENT)
            .build();
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // When
        String token = jwtService.generateToken(testClient);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed and contains correct username
        String username = jwtService.extractUsername(token);
        assertEquals(testClient.getEmail(), username);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldReturnValidToken() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", testClient.getRole().name());
        extraClaims.put("userId", testClient.getId());

        // When
        String token = jwtService.generateToken(extraClaims, testClient);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String username = jwtService.extractUsername(token);
        assertEquals(testClient.getEmail(), username);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateToken(testClient);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals(testClient.getEmail(), username);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        // Given
        String token = jwtService.generateToken(testClient);

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValidAndUserMatches() {
        // Given
        String token = jwtService.generateToken(testClient);

        // When
        boolean isValid = jwtService.isTokenValid(token, testClient);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUserDoesNotMatch() {
        // Given
        String token = jwtService.generateToken(testClient);
        Client differentClient = Client.builder()
            .email("different@example.com")
            .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentClient);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_WhenTokenIsNotExpired() {
        // Given
        String token = jwtService.generateToken(testClient);

        // When
        boolean isExpired = jwtService.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        // Given - Create an expired token
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Date pastDate = new Date(System.currentTimeMillis() - 1000); // 1 second ago
        
        String expiredToken = Jwts.builder()
            .setSubject(testClient.getEmail())
            .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
            .setExpiration(pastDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        // When
        boolean isExpired = jwtService.isTokenExpired(expiredToken);

        // Then
        assertTrue(isExpired);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        // Given
        String token = jwtService.generateToken(testClient);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Then
        assertEquals(testClient.getEmail(), subject);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}
