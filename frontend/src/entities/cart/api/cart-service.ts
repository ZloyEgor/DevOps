import { Product } from '@/entities/product';
import { CartItem } from '../model/cart';
import { NumericId } from '@/shared/types/numeric-id';

// Updated CartItem interface for localStorage
export interface LocalCartItem {
    id: string; // Using string ID for localStorage
    productId: NumericId;
    quantity: number;
    product: Product;
    addedAt: Date;
}

class CartService {
    private readonly CART_KEY = 'shopping_cart';

    // Emit custom event when cart changes
    private emitCartChange() {
        if (typeof window !== 'undefined') {
            window.dispatchEvent(new CustomEvent('cartStateChanged'));
        }
    }

    private getCartFromStorage(): LocalCartItem[] {
        if (typeof window === 'undefined') return [];

        try {
            const cartData = localStorage.getItem(this.CART_KEY);
            if (!cartData) return [];

            const items = JSON.parse(cartData);
            // Convert addedAt strings back to Date objects
            return items.map((item: any) => ({
                ...item,
                addedAt: new Date(item.addedAt),
            }));
        } catch (error) {
            console.error('Error loading cart from localStorage:', error);
            return [];
        }
    }

    private saveCartToStorage(items: LocalCartItem[]): void {
        if (typeof window === 'undefined') return;

        try {
            localStorage.setItem(this.CART_KEY, JSON.stringify(items));
            this.emitCartChange();
        } catch (error) {
            console.error('Error saving cart to localStorage:', error);
        }
    }

    addToCart(productId: NumericId, quantity: number = 1): void {
        // For now, we'll need the product data. We'll enhance this later.
        // This method will be updated when we integrate with product cards
        console.log(`Adding product ${productId} with quantity ${quantity} to cart`);
        this.emitCartChange();
    }

    // Enhanced method that takes full product data
    addProductToCart(product: Product, quantity: number = 1): void {
        const currentCart = this.getCartFromStorage();
        const existingItem = currentCart.find((item) => item.productId === product.id);

        if (existingItem) {
            // Update quantity if item already exists
            existingItem.quantity += quantity;
        } else {
            // Add new item
            const newItem: LocalCartItem = {
                id: `${product.id}-${Date.now()}`, // Unique ID
                productId: product.id,
                quantity,
                product,
                addedAt: new Date(),
            };
            currentCart.push(newItem);
        }

        this.saveCartToStorage(currentCart);
    }

    removeFromCart(cartItemId: string): void {
        const currentCart = this.getCartFromStorage();
        const updatedCart = currentCart.filter((item) => item.id !== cartItemId);
        this.saveCartToStorage(updatedCart);
    }

    updateCartItemQuantity(cartItemId: string, quantity: number): void {
        if (quantity <= 0) {
            this.removeFromCart(cartItemId);
            return;
        }

        const currentCart = this.getCartFromStorage();
        const item = currentCart.find((item) => item.id === cartItemId);

        if (item) {
            item.quantity = quantity;
            this.saveCartToStorage(currentCart);
        }
    }

    getCartItems(): LocalCartItem[] {
        return this.getCartFromStorage();
    }

    getCartItemByProductId(productId: NumericId): LocalCartItem | undefined {
        return this.getCartFromStorage().find((item) => item.productId === productId);
    }

    getTotalItems(): number {
        return this.getCartFromStorage().reduce((total, item) => total + item.quantity, 0);
    }

    getTotalPrice(): number {
        return this.getCartFromStorage().reduce(
            (total, item) => total + item.product.price * item.quantity,
            0
        );
    }

    clearCart(): void {
        this.saveCartToStorage([]);
    }

    // Legacy methods for backward compatibility
    async getCartTotal(): Promise<number> {
        return this.getTotalPrice();
    }

    // Convert to old format for backward compatibility if needed
    async getCartItemsLegacy(): Promise<CartItem[]> {
        const localItems = this.getCartFromStorage();
        return localItems.map((item, index) => ({
            id: index + 1, // Numeric ID for legacy compatibility
            clientId: 1, // Dummy client ID
            product: item.product,
            quantity: item.quantity,
            totalPrice: item.product.price * item.quantity,
        }));
    }
}

export const cartService = new CartService();
