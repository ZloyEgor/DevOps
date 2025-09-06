package ru.itmo.cvetochey.controller.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.itmo.cvetochey.dto.CartItemDto;
import ru.itmo.cvetochey.dto.ProductDto;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.service.CartService;
import ru.itmo.cvetochey.service.ClientUserDetails;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @Mock
    private ClientUserDetails userDetails;

    @Mock
    private Client client;

    @InjectMocks
    private CartController cartController;

    private CartItemDto cartItemDto;
    private List<CartItemDto> cartItems;

    @BeforeEach
    void setUp() {
        // Setup mock authentication
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getClient()).thenReturn(client);
        when(client.getId()).thenReturn(1L);

        // Setup test data
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setPrice(10.0);

        cartItemDto = new CartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setClientId(1L);
        cartItemDto.setProduct(productDto);
        cartItemDto.setQuantity(2);
        cartItemDto.setTotalPrice(20.0);

        cartItems = Arrays.asList(cartItemDto);
    }

    @Test
    void getCartItems_ShouldReturnCartItems() {
        // Given
        when(cartService.getCartItems(1L)).thenReturn(cartItems);

        // When
        ResponseEntity<List<CartItemDto>> result = cartController.getCartItems(authentication);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(cartItems, result.getBody());
        verify(cartService).getCartItems(1L);
    }

    @Test
    void getCartItems_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        when(cartService.getCartItems(1L)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.getCartItems(authentication);
        });
        
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void addToCart_ShouldReturnCartItem() {
        // Given
        when(cartService.addToCart(1L, 1L, 2)).thenReturn(cartItemDto);

        // When
        ResponseEntity<CartItemDto> result = cartController.addToCart(1L, 2, authentication);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(cartItemDto, result.getBody());
        verify(cartService).addToCart(1L, 1L, 2);
    }

    @Test
    void addToCart_WithDefaultQuantity_ShouldUseDefaultValue() {
        // Given
        when(cartService.addToCart(anyLong(), anyLong(), anyInt())).thenReturn(cartItemDto);

        // When
        ResponseEntity<CartItemDto> result = cartController.addToCart(1L, 1, authentication);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(cartService).addToCart(1L, 1L, 1);
    }

    @Test
    void addToCart_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        when(cartService.addToCart(anyLong(), anyLong(), anyInt()))
            .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.addToCart(1L, 2, authentication);
        });
        
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void updateCartItem_ShouldReturnUpdatedCartItem() {
        // Given
        when(cartService.updateCartItem(1L, 1L, 3)).thenReturn(cartItemDto);

        // When
        ResponseEntity<CartItemDto> result = cartController.updateCartItem(1L, 3, authentication);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(cartItemDto, result.getBody());
        verify(cartService).updateCartItem(1L, 1L, 3);
    }

    @Test
    void updateCartItem_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        when(cartService.updateCartItem(anyLong(), anyLong(), anyInt()))
            .thenThrow(new RuntimeException("Cart item not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.updateCartItem(1L, 3, authentication);
        });
        
        assertEquals("Cart item not found", exception.getMessage());
    }

    @Test
    void removeFromCart_ShouldReturnNoContent() {
        // Given
        doNothing().when(cartService).removeFromCart(1L, 1L);

        // When
        ResponseEntity<Void> result = cartController.removeFromCart(1L, authentication);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(cartService).removeFromCart(1L, 1L);
    }

    @Test
    void removeFromCart_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("Cart item not found"))
            .when(cartService).removeFromCart(anyLong(), anyLong());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.removeFromCart(1L, authentication);
        });
        
        assertEquals("Cart item not found", exception.getMessage());
    }

    @Test
    void clearCart_ShouldReturnNoContent() {
        // Given
        doNothing().when(cartService).clearCart(1L);

        // When
        ResponseEntity<Void> result = cartController.clearCart(authentication);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(cartService).clearCart(1L);
    }

    @Test
    void clearCart_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("Database error"))
            .when(cartService).clearCart(anyLong());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.clearCart(authentication);
        });
        
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void getCartTotal_ShouldReturnTotal() {
        // Given
        when(cartService.getCartTotal(1L)).thenReturn(50.0);

        // When
        ResponseEntity<Double> result = cartController.getCartTotal(authentication);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(50.0, result.getBody());
        verify(cartService).getCartTotal(1L);
    }

    @Test
    void getCartTotal_WhenServiceThrowsException_ShouldThrowException() {
        // Given
        when(cartService.getCartTotal(anyLong()))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartController.getCartTotal(authentication);
        });
        
        assertEquals("Database error", exception.getMessage());
    }

}
