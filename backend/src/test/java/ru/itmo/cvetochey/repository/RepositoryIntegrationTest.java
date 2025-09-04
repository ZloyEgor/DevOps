package ru.itmo.cvetochey.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.itmo.cvetochey.model.*;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {

  @Autowired private ClientRepository clientRepository;

  @Autowired private CatalogRepository catalogRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private OrderRepository orderRepository;

  private Client testClient;
  private Catalog testCatalog;
  private Product testProduct;
  private Order testOrder;

  @BeforeEach
  void setUp() {
    // Create test client
    testClient =
        Client.builder()
            .email("test@example.com")
            .username("testuser")
            .password("password")
            .userRole(UserRole.CLIENT)
            .build();
    testClient = clientRepository.save(testClient);

    // Create test catalog
    testCatalog =
        Catalog.builder()
            .name("Spring Flowers")
            .description("Beautiful spring flower collection")
            .catalogType(CatalogType.FLOWERS)
            .build();
    testCatalog = catalogRepository.save(testCatalog);

    // Create test product
    testProduct =
        Product.builder()
            .name("Rose Bouquet")
            .description("Beautiful red roses")
            .price(25.99)
            .pictureUrl("http://example.com/roses.jpg")
            .catalog(testCatalog)
            .build();
    testProduct = productRepository.save(testProduct);

    // Create test order
    testOrder = Order.builder().totalPrice(25.99).client(testClient).product(testProduct).build();
    testOrder = orderRepository.save(testOrder);
  }

  @Test
  void testClientRepositoryCustomMethods() {
    // Test findByEmail
    Optional<Client> foundByEmail = clientRepository.findByEmail("test@example.com");
    assertTrue(foundByEmail.isPresent());
    assertEquals(testClient.getId(), foundByEmail.get().getId());

    // Test findByUsername
    Optional<Client> foundByUsername = clientRepository.findByUsername("testuser");
    assertTrue(foundByUsername.isPresent());
    assertEquals(testClient.getId(), foundByUsername.get().getId());

    // Test findByUserRole
    List<Client> clientsByRole = clientRepository.findByUserRole(UserRole.CLIENT);
    assertFalse(clientsByRole.isEmpty());
    assertEquals(1, clientsByRole.size());
    assertEquals(testClient.getId(), clientsByRole.get(0).getId());

    // Test existsByEmail
    assertTrue(clientRepository.existsByEmail("test@example.com"));
    assertFalse(clientRepository.existsByEmail("nonexistent@example.com"));

    // Test existsByUsername
    assertTrue(clientRepository.existsByUsername("testuser"));
    assertFalse(clientRepository.existsByUsername("nonexistentuser"));
  }

  @Test
  void testCatalogRepositoryCustomMethods() {
    // Test findByCatalogType
    List<Catalog> catalogsByType = catalogRepository.findByCatalogType(CatalogType.FLOWERS);
    assertFalse(catalogsByType.isEmpty());
    assertEquals(1, catalogsByType.size());
    assertEquals(testCatalog.getId(), catalogsByType.get(0).getId());
  }

  @Test
  void testProductRepositoryCustomMethods() {
    // Test findByCatalog
    List<Product> productsByCatalog = productRepository.findByCatalog(testCatalog);
    assertFalse(productsByCatalog.isEmpty());
    assertEquals(1, productsByCatalog.size());
    assertEquals(testProduct.getId(), productsByCatalog.get(0).getId());

    // Test findByCatalogId
    List<Product> productsByCatalogId = productRepository.findByCatalogId(testCatalog.getId());
    assertFalse(productsByCatalogId.isEmpty());
    assertEquals(1, productsByCatalogId.size());
    assertEquals(testProduct.getId(), productsByCatalogId.get(0).getId());

    // Test findByNameContainingIgnoreCase
    List<Product> productsByName = productRepository.findByNameContainingIgnoreCase("rose");
    assertFalse(productsByName.isEmpty());
    assertEquals(1, productsByName.size());
    assertEquals(testProduct.getId(), productsByName.get(0).getId());

    List<Product> productsByNameUpperCase =
        productRepository.findByNameContainingIgnoreCase("ROSE");
    assertFalse(productsByNameUpperCase.isEmpty());
    assertEquals(1, productsByNameUpperCase.size());

    // Test findByPriceBetween
    List<Product> productsByPriceRange = productRepository.findByPriceBetween(20.0, 30.0);
    assertFalse(productsByPriceRange.isEmpty());
    assertEquals(1, productsByPriceRange.size());
    assertEquals(testProduct.getId(), productsByPriceRange.get(0).getId());

    List<Product> productsOutOfRange = productRepository.findByPriceBetween(30.0, 40.0);
    assertTrue(productsOutOfRange.isEmpty());
  }

  @Test
  void testOrderRepositoryCustomMethods() {
    // Test findByClient
    List<Order> ordersByClient = orderRepository.findByClient(testClient);
    assertFalse(ordersByClient.isEmpty());
    assertEquals(1, ordersByClient.size());
    assertEquals(testOrder.getId(), ordersByClient.get(0).getId());

    // Test findByClientId
    List<Order> ordersByClientId = orderRepository.findByClientId(testClient.getId());
    assertFalse(ordersByClientId.isEmpty());
    assertEquals(1, ordersByClientId.size());
    assertEquals(testOrder.getId(), ordersByClientId.get(0).getId());

    // Test findByProduct
    List<Order> ordersByProduct = orderRepository.findByProduct(testProduct);
    assertFalse(ordersByProduct.isEmpty());
    assertEquals(1, ordersByProduct.size());
    assertEquals(testOrder.getId(), ordersByProduct.get(0).getId());

    // Test findByProductId
    List<Order> ordersByProductId = orderRepository.findByProductId(testProduct.getId());
    assertFalse(ordersByProductId.isEmpty());
    assertEquals(1, ordersByProductId.size());
    assertEquals(testOrder.getId(), ordersByProductId.get(0).getId());

    // Test findByTotalPriceBetween
    List<Order> ordersByPriceRange = orderRepository.findByTotalPriceBetween(20.0, 30.0);
    assertFalse(ordersByPriceRange.isEmpty());
    assertEquals(1, ordersByPriceRange.size());
    assertEquals(testOrder.getId(), ordersByPriceRange.get(0).getId());

    List<Order> ordersOutOfRange = orderRepository.findByTotalPriceBetween(30.0, 40.0);
    assertTrue(ordersOutOfRange.isEmpty());
  }

  @Test
  void testBasicCrudOperations() {
    // Test Client CRUD
    Client newClient =
        Client.builder()
            .email("new@example.com")
            .username("newuser")
            .password("newpassword")
            .userRole(UserRole.ADMIN)
            .build();

    Client savedClient = clientRepository.save(newClient);
    assertNotNull(savedClient.getId());

    Optional<Client> foundClient = clientRepository.findById(savedClient.getId());
    assertTrue(foundClient.isPresent());
    assertEquals("new@example.com", foundClient.get().getEmail());

    savedClient.setEmail("updated@example.com");
    Client updatedClient = clientRepository.save(savedClient);
    assertEquals("updated@example.com", updatedClient.getEmail());

    clientRepository.delete(savedClient);
    assertFalse(clientRepository.findById(savedClient.getId()).isPresent());

    // Test Catalog CRUD
    Catalog newCatalog =
        Catalog.builder()
            .name("Summer Flowers")
            .description("Summer collection")
            .catalogType(CatalogType.FLOWERS)
            .build();

    Catalog savedCatalog = catalogRepository.save(newCatalog);
    assertNotNull(savedCatalog.getId());

    Optional<Catalog> foundCatalog = catalogRepository.findById(savedCatalog.getId());
    assertTrue(foundCatalog.isPresent());
    assertEquals("Summer Flowers", foundCatalog.get().getName());

    savedCatalog.setName("Updated Summer Flowers");
    Catalog updatedCatalog = catalogRepository.save(savedCatalog);
    assertEquals("Updated Summer Flowers", updatedCatalog.getName());

    catalogRepository.delete(savedCatalog);
    assertFalse(catalogRepository.findById(savedCatalog.getId()).isPresent());

    // Test Product CRUD
    Product newProduct =
        Product.builder()
            .name("Tulip Bouquet")
            .description("Beautiful tulips")
            .price(19.99)
            .pictureUrl("http://example.com/tulips.jpg")
            .catalog(testCatalog)
            .build();

    Product savedProduct = productRepository.save(newProduct);
    assertNotNull(savedProduct.getId());

    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
    assertTrue(foundProduct.isPresent());
    assertEquals("Tulip Bouquet", foundProduct.get().getName());

    savedProduct.setName("Updated Tulip Bouquet");
    Product updatedProduct = productRepository.save(savedProduct);
    assertEquals("Updated Tulip Bouquet", updatedProduct.getName());

    productRepository.delete(savedProduct);
    assertFalse(productRepository.findById(savedProduct.getId()).isPresent());

    // Test Order CRUD
    Order newOrder =
        Order.builder().totalPrice(19.99).client(testClient).product(testProduct).build();

    Order savedOrder = orderRepository.save(newOrder);
    assertNotNull(savedOrder.getId());

    Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
    assertTrue(foundOrder.isPresent());
    assertEquals(19.99, foundOrder.get().getTotalPrice());

    savedOrder.setTotalPrice(29.99);
    Order updatedOrder = orderRepository.save(savedOrder);
    assertEquals(29.99, updatedOrder.getTotalPrice());

    orderRepository.delete(savedOrder);
    assertFalse(orderRepository.findById(savedOrder.getId()).isPresent());
  }
}
