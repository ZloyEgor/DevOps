package ru.itmo.cvetochey.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.cvetochey.dto.CartItemDto;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.mapper.CartItemMapper;
import ru.itmo.cvetochey.model.CartItem;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.CartItemRepository;
import ru.itmo.cvetochey.repository.ClientRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartService cartService;

    private Client testClient;
    private Product testProduct;
    private CartItem testCartItem;
    private CartItemDto testCartItemDto;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .build();

        testProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .price(10.0)
            .build();

        testCartItem = CartItem.builder()
            .id(1L)
            .client(testClient)
            .product(testProduct)
            .quantity(2)
            .build();

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(10.0);

        testCartItemDto = new CartItemDto();
        testCartItemDto.setId(1L);
        testCartItemDto.setClientId(1L);
        testCartItemDto.setProduct(productDto);
        testCartItemDto.setQuantity(2);
    }

    @Test
    void getCartItems_ShouldReturnCartItems() {
        // Given
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        List<CartItemDto> cartItemDtos = Arrays.asList(testCartItemDto);

        when(cartItemRepository.findByClientId(1L)).thenReturn(cartItems);
        when(cartItemMapper.toDto(testCartItem)).thenReturn(testCartItemDto);

        // When
        List<CartItemDto> result = cartService.getCartItems(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testCartItemDto, result.get(0));
        verify(cartItemRepository).findByClientId(1L);
        verify(cartItemMapper).toDto(testCartItem);
    }

    @Test
    void addToCart_ShouldCreateNewCartItem_WhenItemDoesNotExist() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByClientIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartItemMapper.toDto(testCartItem)).thenReturn(testCartItemDto);

        // When
        CartItemDto result = cartService.addToCart(1L, 1L, 2);

        // Then
        assertEquals(testCartItemDto, result);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_ShouldUpdateExistingCartItem_WhenItemExists() {
        // Given
        CartItem existingItem = CartItem.builder()
            .id(1L)
            .client(testClient)
            .product(testProduct)
            .quantity(1)
            .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByClientIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
        when(cartItemMapper.toDto(existingItem)).thenReturn(testCartItemDto);

        // When
        CartItemDto result = cartService.addToCart(1L, 1L, 2);

        // Then
        assertEquals(3, existingItem.getQuantity()); // 1 + 2
        assertEquals(testCartItemDto, result);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addToCart_ShouldThrowException_WhenClientNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartService.addToCart(1L, 1L, 2));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void addToCart_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartService.addToCart(1L, 1L, 2));
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void updateCartItem_ShouldUpdateQuantity() {
        // Given
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartItemMapper.toDto(any(CartItem.class))).thenReturn(testCartItemDto);

        // When
        CartItemDto result = cartService.updateCartItem(1L, 1L, 5);

        // Then
        assertEquals(testCartItemDto, result);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_ShouldThrowException_WhenCartItemNotFound() {
        // Given
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartService.updateCartItem(1L, 1L, 5));
        assertEquals("Cart item not found", exception.getMessage());
        verify(cartItemRepository).findById(1L);
    }

    @Test
    void removeFromCart_ShouldRemoveItem_WhenItemBelongsToClient() {
        // Given
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // When
        cartService.removeFromCart(1L, 1L);

        // Then
        verify(cartItemRepository).delete(testCartItem);
    }

    @Test
    void removeFromCart_ShouldThrowException_WhenItemNotFound() {
        // Given
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartService.removeFromCart(1L, 1L));
        assertEquals("Cart item not found", exception.getMessage());
    }

    @Test
    void removeFromCart_ShouldThrowException_WhenUnauthorizedAccess() {
        // Given
        Client otherClient = Client.builder().id(2L).build();
        CartItem otherClientItem = CartItem.builder()
            .id(1L)
            .client(otherClient)
            .product(testProduct)
            .quantity(1)
            .build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(otherClientItem));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartService.removeFromCart(1L, 1L));
        assertEquals("Unauthorized access to cart item", exception.getMessage());
    }

    @Test
    void clearCart_ShouldDeleteAllClientItems() {
        // When
        cartService.clearCart(1L);

        // Then
        verify(cartItemRepository).deleteByClientId(1L);
    }

    @Test
    void getCartTotal_ShouldCalculateTotal() {
        // Given
        CartItem item1 = CartItem.builder()
            .product(Product.builder().price(10.0).build())
            .quantity(2)
            .build();
        CartItem item2 = CartItem.builder()
            .product(Product.builder().price(5.0).build())
            .quantity(3)
            .build();

        List<CartItem> cartItems = Arrays.asList(item1, item2);
        when(cartItemRepository.findByClientId(1L)).thenReturn(cartItems);

        // When
        Double total = cartService.getCartTotal(1L);

        // Then
        assertEquals(35.0, total); // (10.0 * 2) + (5.0 * 3) = 20 + 15 = 35
        verify(cartItemRepository).findByClientId(1L);
    }

    @Test
    void getCartTotal_ShouldReturnZero_WhenCartIsEmpty() {
        // Given
        when(cartItemRepository.findByClientId(1L)).thenReturn(Arrays.asList());

        // When
        Double total = cartService.getCartTotal(1L);

        // Then
        assertEquals(0.0, total);
    }
}
