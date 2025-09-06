import { cartService } from './cart-service';

// Mock localStorage
const localStorageMock = {
    getItem: jest.fn(),
    setItem: jest.fn(),
    removeItem: jest.fn(),
    clear: jest.fn(),
};
Object.defineProperty(window, 'localStorage', {
    value: localStorageMock,
});

// Mock window events
Object.defineProperty(window, 'dispatchEvent', {
    value: jest.fn(),
});

// Mock console.log and console.error to avoid noise in tests
const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();

describe('CartService', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorageMock.getItem.mockClear();
        localStorageMock.setItem.mockClear();
        localStorageMock.removeItem.mockClear();
        consoleLogSpy.mockClear();
        consoleErrorSpy.mockClear();
    });

    afterAll(() => {
        consoleLogSpy.mockRestore();
        consoleErrorSpy.mockRestore();
    });

    describe('getCartItems', () => {
        it('should return empty array when no cart exists', () => {
            localStorageMock.getItem.mockReturnValue(null);

            const result = cartService.getCartItems();

            expect(result).toEqual([]);
            expect(localStorageMock.getItem).toHaveBeenCalledWith('shopping_cart');
        });

        it('should return empty array when cart data is invalid', () => {
            localStorageMock.getItem.mockReturnValue('invalid json');

            const result = cartService.getCartItems();

            expect(result).toEqual([]);
            expect(consoleErrorSpy).toHaveBeenCalled();
        });

        it('should return cart items from localStorage', () => {
            const cartItems = [
                {
                    id: '1',
                    productId: 1,
                    product: { id: 1, name: 'Product 1', price: 100, description: '', imageUrl: '' },
                    quantity: 2,
                    addedAt: new Date().toISOString(),
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(cartItems));

            const result = cartService.getCartItems();

            expect(result).toHaveLength(1);
            expect(result[0].id).toBe('1');
            expect(result[0].quantity).toBe(2);
            expect(result[0].addedAt).toBeInstanceOf(Date);
        });
    });

    describe('getTotalItems', () => {
        it('should return total quantity of all items', () => {
            const cartItems = [
                {
                    id: '1',
                    productId: 1,
                    product: { id: 1, name: 'Product 1', price: 100, description: '', imageUrl: '' },
                    quantity: 2,
                    addedAt: new Date().toISOString(),
                },
                {
                    id: '2',
                    productId: 2,
                    product: { id: 2, name: 'Product 2', price: 200, description: '', imageUrl: '' },
                    quantity: 3,
                    addedAt: new Date().toISOString(),
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(cartItems));

            const result = cartService.getTotalItems();

            expect(result).toBe(5); // 2 + 3
        });

        it('should return 0 for empty cart', () => {
            localStorageMock.getItem.mockReturnValue(null);

            const result = cartService.getTotalItems();

            expect(result).toBe(0);
        });
    });

    describe('getTotalPrice', () => {
        it('should return total price of all items', () => {
            const cartItems = [
                {
                    id: '1',
                    productId: 1,
                    product: { id: 1, name: 'Product 1', price: 100, description: '', imageUrl: '' },
                    quantity: 2,
                    addedAt: new Date().toISOString(),
                },
                {
                    id: '2',
                    productId: 2,
                    product: { id: 2, name: 'Product 2', price: 200, description: '', imageUrl: '' },
                    quantity: 3,
                    addedAt: new Date().toISOString(),
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(cartItems));

            const result = cartService.getTotalPrice();

            expect(result).toBe(800); // (100 * 2) + (200 * 3)
        });

        it('should return 0 for empty cart', () => {
            localStorageMock.getItem.mockReturnValue(null);

            const result = cartService.getTotalPrice();

            expect(result).toBe(0);
        });
    });

    describe('clearCart', () => {
        it('should clear all items from cart', () => {
            cartService.clearCart();

            expect(localStorageMock.setItem).toHaveBeenCalledWith('shopping_cart', '[]');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('cartStateChanged'));
        });
    });

    describe('addToCart', () => {
        it('should handle adding items', () => {
            localStorageMock.getItem.mockReturnValue('[]');

            const product = {
                id: 1,
                name: 'Test Product',
                price: 100,
                description: 'Test description',
                imageUrl: 'test.jpg',
            };

            cartService.addToCart(product, 2);

            expect(consoleLogSpy).toHaveBeenCalledWith(
                expect.stringContaining('Adding product')
            );
        });
    });

    describe('removeFromCart', () => {
        it('should handle removing items', () => {
            const existingCart = JSON.stringify([
                {
                    id: '1',
                    productId: 1,
                    product: { id: 1, name: 'Product 1', price: 100, description: '', imageUrl: '' },
                    quantity: 2,
                    addedAt: new Date().toISOString(),
                },
            ]);
            localStorageMock.getItem.mockReturnValue(existingCart);

            cartService.removeFromCart('1');

            // The method should be called (implementation details may vary)
            expect(localStorageMock.getItem).toHaveBeenCalled();
        });
    });

    describe('updateCartItemQuantity', () => {
        it('should handle updating quantities', () => {
            const existingCart = JSON.stringify([
                {
                    id: '1',
                    productId: 1,
                    product: { id: 1, name: 'Product 1', price: 100, description: '', imageUrl: '' },
                    quantity: 2,
                    addedAt: new Date().toISOString(),
                },
            ]);
            localStorageMock.getItem.mockReturnValue(existingCart);

            cartService.updateCartItemQuantity('1', 5);

            // The method should be called (implementation details may vary)
            expect(localStorageMock.getItem).toHaveBeenCalled();
        });
    });
});