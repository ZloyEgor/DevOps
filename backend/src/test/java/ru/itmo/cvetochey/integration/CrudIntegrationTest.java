package ru.itmo.cvetochey.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.itmo.cvetochey.dto.*;
import ru.itmo.cvetochey.model.CatalogType;
import ru.itmo.cvetochey.model.UserRole;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CrudIntegrationTest {

  @Autowired private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  @Transactional
  void testClientCrudOperations() throws Exception {
    // CREATE - Test client creation
    ClientCreateDto createDto = new ClientCreateDto();
    createDto.setEmail("test@example.com");
    createDto.setUsername("testuser");
    createDto.setPassword("password123");
    createDto.setUserRole(UserRole.CLIENT);

    String clientJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.userRole").value("CLIENT"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    ClientDto createdClient = objectMapper.readValue(clientJson, ClientDto.class);
    Long clientId = createdClient.getId();

    // READ - Test getting all clients
    mockMvc
        .perform(get("/cvet-ochey/api/v1/clients"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(clientId))
        .andExpect(jsonPath("$[0].email").value("test@example.com"));

    // READ - Test getting client by ID
    mockMvc
        .perform(get("/cvet-ochey/api/v1/clients/" + clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(clientId))
        .andExpect(jsonPath("$.email").value("test@example.com"));

    // UPDATE - Test updating client
    createDto.setEmail("updated@example.com");
    createDto.setUsername("updateduser");

    mockMvc
        .perform(
            put("/cvet-ochey/api/v1/clients/" + clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("updated@example.com"))
        .andExpect(jsonPath("$.username").value("updateduser"));

    // DELETE - Test deleting client
    mockMvc
        .perform(delete("/cvet-ochey/api/v1/clients/" + clientId))
        .andExpect(status().isNoContent());

    // Verify client is deleted
    mockMvc.perform(get("/cvet-ochey/api/v1/clients/" + clientId)).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void testCatalogCrudOperations() throws Exception {
    // CREATE - Test catalog creation
    CatalogDto catalogDto = new CatalogDto();
    catalogDto.setName("Spring Flowers");
    catalogDto.setDescription("Beautiful spring flower collection");
    catalogDto.setCatalogType(CatalogType.FLOWERS);

    String catalogJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/catalog")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(catalogDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Spring Flowers"))
            .andExpect(jsonPath("$.description").value("Beautiful spring flower collection"))
            .andExpect(jsonPath("$.catalogType").value("FLOWERS"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    CatalogDto createdCatalog = objectMapper.readValue(catalogJson, CatalogDto.class);
    Long catalogId = createdCatalog.getId();

    // READ - Test getting all catalogs
    mockMvc
        .perform(get("/cvet-ochey/api/v1/catalog"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(catalogId));

    // READ - Test getting catalog by ID
    mockMvc
        .perform(get("/cvet-ochey/api/v1/catalog/" + catalogId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(catalogId))
        .andExpect(jsonPath("$.name").value("Spring Flowers"));

    // UPDATE - Test updating catalog
    catalogDto.setName("Updated Spring Flowers");
    catalogDto.setDescription("Updated description");

    mockMvc
        .perform(
            put("/cvet-ochey/api/v1/catalog/" + catalogId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catalogDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Spring Flowers"))
        .andExpect(jsonPath("$.description").value("Updated description"));

    // DELETE - Test deleting catalog
    mockMvc
        .perform(delete("/cvet-ochey/api/v1/catalog/" + catalogId))
        .andExpect(status().isNoContent());

    // Verify catalog is deleted
    mockMvc
        .perform(get("/cvet-ochey/api/v1/catalog/" + catalogId))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void testProductCrudOperations() throws Exception {
    // First create a catalog for the product
    CatalogDto catalogDto = new CatalogDto();
    catalogDto.setName("Test Catalog");
    catalogDto.setDescription("Test catalog description");
    catalogDto.setCatalogType(CatalogType.FLOWERS);

    String catalogJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/catalog")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(catalogDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    CatalogDto createdCatalog = objectMapper.readValue(catalogJson, CatalogDto.class);
    Long catalogId = createdCatalog.getId();

    // CREATE - Test product creation
    ProductDto productDto = new ProductDto();
    productDto.setName("Rose Bouquet");
    productDto.setDescription("Beautiful red roses");
    productDto.setPrice(25.99);
    productDto.setPictureUrl("http://example.com/roses.jpg");
    productDto.setCatalogId(catalogId);

    String productJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Rose Bouquet"))
            .andExpect(jsonPath("$.price").value(25.99))
            .andExpect(jsonPath("$.catalogId").value(catalogId))
            .andReturn()
            .getResponse()
            .getContentAsString();

    ProductDto createdProduct = objectMapper.readValue(productJson, ProductDto.class);
    Long productId = createdProduct.getId();

    // READ - Test getting all products
    mockMvc
        .perform(get("/cvet-ochey/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(productId));

    // READ - Test getting product by ID
    mockMvc
        .perform(get("/cvet-ochey/api/v1/products/" + productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(productId))
        .andExpect(jsonPath("$.name").value("Rose Bouquet"));

    // UPDATE - Test updating product
    productDto.setName("Updated Rose Bouquet");
    productDto.setPrice(29.99);

    mockMvc
        .perform(
            put("/cvet-ochey/api/v1/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Rose Bouquet"))
        .andExpect(jsonPath("$.price").value(29.99));

    // DELETE - Test deleting product
    mockMvc
        .perform(delete("/cvet-ochey/api/v1/products/" + productId))
        .andExpect(status().isNoContent());

    // Verify product is deleted
    mockMvc
        .perform(get("/cvet-ochey/api/v1/products/" + productId))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void testOrderCrudOperations() throws Exception {
    // First create a client
    ClientCreateDto clientDto = new ClientCreateDto();
    clientDto.setEmail("order@example.com");
    clientDto.setUsername("orderuser");
    clientDto.setPassword("password123");
    clientDto.setUserRole(UserRole.CLIENT);

    String clientJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clientDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ClientDto createdClient = objectMapper.readValue(clientJson, ClientDto.class);
    Long clientId = createdClient.getId();

    // Create a catalog and product
    CatalogDto catalogDto = new CatalogDto();
    catalogDto.setName("Order Catalog");
    catalogDto.setDescription("Catalog for orders");
    catalogDto.setCatalogType(CatalogType.FLOWERS);

    String catalogJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/catalog")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(catalogDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    CatalogDto createdCatalog = objectMapper.readValue(catalogJson, CatalogDto.class);

    ProductDto productDto = new ProductDto();
    productDto.setName("Order Product");
    productDto.setDescription("Product for orders");
    productDto.setPrice(35.99);
    productDto.setPictureUrl("http://example.com/product.jpg");
    productDto.setCatalogId(createdCatalog.getId());

    String productJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ProductDto createdProduct = objectMapper.readValue(productJson, ProductDto.class);
    Long productId = createdProduct.getId();

    // CREATE - Test order creation
    OrderDto orderDto = new OrderDto();
    orderDto.setTotalPrice(35.99);
    orderDto.setClientId(clientId);
    orderDto.setProductId(productId);

    String orderJson =
        mockMvc
            .perform(
                post("/cvet-ochey/api/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPrice").value(35.99))
            .andExpect(jsonPath("$.clientId").value(clientId))
            .andExpect(jsonPath("$.productId").value(productId))
            .andReturn()
            .getResponse()
            .getContentAsString();

    OrderDto createdOrder = objectMapper.readValue(orderJson, OrderDto.class);
    Long orderId = createdOrder.getId();

    // READ - Test getting all orders
    mockMvc
        .perform(get("/cvet-ochey/api/v1/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(orderId));

    // READ - Test getting order by ID
    mockMvc
        .perform(get("/cvet-ochey/api/v1/orders/" + orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId))
        .andExpect(jsonPath("$.totalPrice").value(35.99));

    // UPDATE - Test updating order
    orderDto.setTotalPrice(39.99);

    mockMvc
        .perform(
            put("/cvet-ochey/api/v1/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPrice").value(39.99));

    // DELETE - Test deleting order
    mockMvc
        .perform(delete("/cvet-ochey/api/v1/orders/" + orderId))
        .andExpect(status().isNoContent());

    // Verify order is deleted
    mockMvc.perform(get("/cvet-ochey/api/v1/orders/" + orderId)).andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void testValidationErrors() throws Exception {
    // Test creating client with duplicate email
    ClientCreateDto clientDto = new ClientCreateDto();
    clientDto.setEmail("duplicate@example.com");
    clientDto.setUsername("user1");
    clientDto.setPassword("password");
    clientDto.setUserRole(UserRole.CLIENT);

    // Create first client
    mockMvc
        .perform(
            post("/cvet-ochey/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDto)))
        .andExpect(status().isOk());

    // Try to create second client with same email
    clientDto.setUsername("user2");
    mockMvc
        .perform(
            post("/cvet-ochey/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDto)))
        .andExpect(status().isBadRequest());

    // Test creating order with invalid client ID
    OrderDto orderDto = new OrderDto();
    orderDto.setTotalPrice(25.99);
    orderDto.setClientId(999L); // Non-existent client
    orderDto.setProductId(null);

    mockMvc
        .perform(
            post("/cvet-ochey/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
        .andExpect(status().isBadRequest());

    // Test creating product with invalid catalog ID
    ProductDto productDto = new ProductDto();
    productDto.setName("Test Product");
    productDto.setDescription("Test description");
    productDto.setPrice(15.99);
    productDto.setCatalogId(999L); // Non-existent catalog

    mockMvc
        .perform(
            post("/cvet-ochey/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }
}
