package ru.itmo.cvetochey.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.cvetochey.dto.OrderDto;
import ru.itmo.cvetochey.mapper.OrderMapper;
import ru.itmo.cvetochey.model.*;
import ru.itmo.cvetochey.repository.ClientRepository;
import ru.itmo.cvetochey.repository.OrderRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock private OrderRepository orderRepository;

  @Mock private ClientRepository clientRepository;

  @Mock private ProductRepository productRepository;

  @Mock private OrderMapper orderMapper;

  @InjectMocks private OrderController orderController;

  private ObjectMapper objectMapper;
  private Order testOrder;
  private OrderDto testOrderDto;
  private Client testClient;
  private Product testProduct;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    testClient =
        Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("password")
            .userRole(UserRole.CLIENT)
            .build();

    Catalog testCatalog =
        Catalog.builder()
            .id(1L)
            .name("Spring Flowers")
            .description("Beautiful spring flower collection")
            .catalogType(CatalogType.FLOWERS)
            .build();

    testProduct =
        Product.builder()
            .id(1L)
            .name("Rose Bouquet")
            .description("Beautiful red roses")
            .price(25.99)
            .pictureUrl("http://example.com/roses.jpg")
            .catalog(testCatalog)
            .build();

    testOrder =
        Order.builder().id(1L).totalPrice(25.99).client(testClient).product(testProduct).build();

    testOrderDto = new OrderDto();
    testOrderDto.setId(1L);
    testOrderDto.setTotalPrice(25.99);
    testOrderDto.setClientId(1L);
    testOrderDto.setProductId(1L);
  }

  @Test
  void getAll_ShouldReturnAllOrders() {
    // Given
    List<Order> orders = Arrays.asList(testOrder);
    when(orderRepository.findAll()).thenReturn(orders);
    when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDto);

    // When
    List<OrderDto> result = orderController.getAll();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testOrderDto, result.get(0));

    verify(orderRepository).findAll();
    verify(orderMapper).toDto(testOrder);
  }

  @Test
  void getOne_WhenOrderExists_ShouldReturnOrder() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    ResponseEntity<OrderDto> response = orderController.getOne(1L);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(testOrderDto, response.getBody());

    verify(orderRepository).findById(1L);
    verify(orderMapper).toDto(testOrder);
  }

  @Test
  void getOne_WhenOrderNotExists_ShouldReturnNotFound() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<OrderDto> response = orderController.getOne(1L);

    // Then
    assertEquals(404, response.getStatusCodeValue());
    assertNull(response.getBody());

    verify(orderRepository).findById(1L);
    verify(orderMapper, never()).toDto(any());
  }

  @Test
  void create_WithValidData_ShouldCreateOrder() {
    // Given
    when(orderMapper.toEntity(testOrderDto)).thenReturn(testOrder);
    when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(orderRepository.save(testOrder)).thenReturn(testOrder);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    ResponseEntity<OrderDto> response = orderController.create(testOrderDto);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(testOrderDto, response.getBody());

    verify(orderMapper).toEntity(testOrderDto);
    verify(clientRepository).findById(1L);
    verify(productRepository).findById(1L);
    verify(orderRepository).save(testOrder);
    verify(orderMapper).toDto(testOrder);
  }

  @Test
  void create_WithInvalidClientId_ShouldReturnBadRequest() {
    // Given
    when(orderMapper.toEntity(testOrderDto)).thenReturn(testOrder);
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<OrderDto> response = orderController.create(testOrderDto);

    // Then
    assertEquals(400, response.getStatusCodeValue());
    assertNull(response.getBody());

    verify(clientRepository).findById(1L);
    verify(orderRepository, never()).save(any());
  }

  @Test
  void create_WithInvalidProductId_ShouldReturnBadRequest() {
    // Given
    when(orderMapper.toEntity(testOrderDto)).thenReturn(testOrder);
    when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<OrderDto> response = orderController.create(testOrderDto);

    // Then
    assertEquals(400, response.getStatusCodeValue());
    assertNull(response.getBody());

    verify(clientRepository).findById(1L);
    verify(productRepository).findById(1L);
    verify(orderRepository, never()).save(any());
  }

  @Test
  void create_WithNullClientAndProductIds_ShouldCreateOrder() {
    // Given
    testOrderDto.setClientId(null);
    testOrderDto.setProductId(null);
    when(orderMapper.toEntity(testOrderDto)).thenReturn(testOrder);
    when(orderRepository.save(testOrder)).thenReturn(testOrder);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    ResponseEntity<OrderDto> response = orderController.create(testOrderDto);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());

    verify(orderRepository).save(testOrder);
    verify(clientRepository, never()).findById(anyLong());
    verify(productRepository, never()).findById(anyLong());
  }

  @Test
  void update_WhenOrderExists_ShouldUpdateOrder() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
    when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(orderRepository.save(testOrder)).thenReturn(testOrder);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    ResponseEntity<OrderDto> response = orderController.update(1L, testOrderDto);

    // Then
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());

    verify(orderRepository).findById(1L);
    verify(clientRepository).findById(1L);
    verify(productRepository).findById(1L);
    verify(orderRepository).save(testOrder);
  }

  @Test
  void update_WhenOrderNotExists_ShouldReturnNotFound() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // When
    ResponseEntity<OrderDto> response = orderController.update(1L, testOrderDto);

    // Then
    assertEquals(404, response.getStatusCodeValue());

    verify(orderRepository).findById(1L);
    verify(orderRepository, never()).save(any());
  }

  @Test
  void delete_WhenOrderExists_ShouldDeleteOrder() {
    // Given
    when(orderRepository.existsById(1L)).thenReturn(true);

    // When
    ResponseEntity<Void> response = orderController.delete(1L);

    // Then
    assertEquals(204, response.getStatusCodeValue());

    verify(orderRepository).existsById(1L);
    verify(orderRepository).deleteById(1L);
  }

  @Test
  void delete_WhenOrderNotExists_ShouldReturnNotFound() {
    // Given
    when(orderRepository.existsById(1L)).thenReturn(false);

    // When
    ResponseEntity<Void> response = orderController.delete(1L);

    // Then
    assertEquals(404, response.getStatusCodeValue());

    verify(orderRepository).existsById(1L);
    verify(orderRepository, never()).deleteById(anyLong());
  }

  @Test
  void getByClientId_ShouldReturnOrdersByClient() {
    // Given
    List<Order> orders = Arrays.asList(testOrder);
    when(orderRepository.findByClientId(1L)).thenReturn(orders);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    List<OrderDto> result = orderController.getByClientId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testOrderDto, result.get(0));

    verify(orderRepository).findByClientId(1L);
    verify(orderMapper).toDto(testOrder);
  }

  @Test
  void getByProductId_ShouldReturnOrdersByProduct() {
    // Given
    List<Order> orders = Arrays.asList(testOrder);
    when(orderRepository.findByProductId(1L)).thenReturn(orders);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    List<OrderDto> result = orderController.getByProductId(1L);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testOrderDto, result.get(0));

    verify(orderRepository).findByProductId(1L);
    verify(orderMapper).toDto(testOrder);
  }

  @Test
  void getByPriceRange_ShouldReturnOrdersInRange() {
    // Given
    List<Order> orders = Arrays.asList(testOrder);
    when(orderRepository.findByTotalPriceBetween(20.0, 30.0)).thenReturn(orders);
    when(orderMapper.toDto(testOrder)).thenReturn(testOrderDto);

    // When
    List<OrderDto> result = orderController.getByPriceRange(20.0, 30.0);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testOrderDto, result.get(0));

    verify(orderRepository).findByTotalPriceBetween(20.0, 30.0);
    verify(orderMapper).toDto(testOrder);
  }
}
