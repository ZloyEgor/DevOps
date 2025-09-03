import { API_CONFIG } from '@/shared/config/api';
import { authService } from '@/entities/auth';
import { CartItem } from '../model/cart';

class CartService {
    private getAuthHeaders() {
        return authService.getAuthHeaders();
    }

    async getCartItems(): Promise<CartItem[]> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart`, {
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to fetch cart items');
        }

        return response.json();
    }

    async addToCart(productId: number, quantity: number = 1): Promise<CartItem> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart/add?productId=${productId}&quantity=${quantity}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to add item to cart');
        }

        return response.json();
    }

    async updateCartItem(itemId: number, quantity: number): Promise<CartItem> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart/update/${itemId}?quantity=${quantity}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to update cart item');
        }

        return response.json();
    }

    async removeFromCart(itemId: number): Promise<void> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart/remove/${itemId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to remove item from cart');
        }
    }

    async clearCart(): Promise<void> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart/clear`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to clear cart');
        }
    }

    async getCartTotal(): Promise<number> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}/cart/total`, {
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders(),
            },
        });

        if (!response.ok) {
            throw new Error('Failed to get cart total');
        }

        return response.json();
    }
}

export const cartService = new CartService();
