package ru.itmo.cvetochey.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.itmo.cvetochey.dto.CartItemDto;
import ru.itmo.cvetochey.service.CartService;
import ru.itmo.cvetochey.service.ClientUserDetails;

import java.util.List;

@RestController
@RequestMapping("/cvet-ochey/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCartItems(Authentication authentication) {
        Long clientId = getClientId(authentication);
        List<CartItemDto> items = cartService.getCartItems(clientId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication authentication) {
        Long clientId = getClientId(authentication);
        CartItemDto item = cartService.addToCart(clientId, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            Authentication authentication) {
        Long clientId = getClientId(authentication);
        CartItemDto item = cartService.updateCartItem(clientId, itemId, quantity);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        Long clientId = getClientId(authentication);
        cartService.removeFromCart(clientId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long clientId = getClientId(authentication);
        cartService.clearCart(clientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getCartTotal(Authentication authentication) {
        Long clientId = getClientId(authentication);
        Double total = cartService.getCartTotal(clientId);
        return ResponseEntity.ok(total);
    }

    private Long getClientId(Authentication authentication) {
        ClientUserDetails userDetails = (ClientUserDetails) authentication.getPrincipal();
        return userDetails.getClient().getId();
    }
}
