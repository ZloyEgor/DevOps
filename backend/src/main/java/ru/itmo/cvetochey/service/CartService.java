package ru.itmo.cvetochey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.cvetochey.dto.CartItemDto;
import ru.itmo.cvetochey.mapper.CartItemMapper;
import ru.itmo.cvetochey.model.CartItem;
import ru.itmo.cvetochey.model.Client;
import ru.itmo.cvetochey.model.Product;
import ru.itmo.cvetochey.repository.CartItemRepository;
import ru.itmo.cvetochey.repository.ClientRepository;
import ru.itmo.cvetochey.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;

    public List<CartItemDto> getCartItems(Long clientId) {
        return cartItemRepository.findByClientId(clientId)
                .stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemDto addToCart(Long clientId, Long productId, Integer quantity) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        var existingItem = cartItemRepository.findByClientIdAndProductId(clientId, productId);
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = CartItem.builder()
                    .client(client)
                    .product(product)
                    .quantity(quantity)
                    .build();
        }

        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Transactional
    public CartItemDto updateCartItem(Long clientId, Long itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the client
        if (!cartItem.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Transactional
    public void removeFromCart(Long clientId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Verify the item belongs to the client
        if (!cartItem.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Long clientId) {
        cartItemRepository.deleteByClientId(clientId);
    }

    public Double getCartTotal(Long clientId) {
        return cartItemRepository.findByClientId(clientId)
                .stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
