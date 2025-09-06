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

describe('CartService', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorageMock.getItem.mockClear();
        localStorageMock.setItem.mockClear();
        localStorageMock.removeItem.mockClear();
    });

    describe('addToCart', () => {
        it('should add new item to empty cart', () => {
            localStorageMock.getItem.mockReturnValue(null);

            const product = {
                id: 1,
                name: 'Test Product',
                price: 100,
                description: 'Test description',
                imageUrl: 'test.jpg',
            };

            cartService.addToCart(product, 2);

            const expectedCart = [
                {
                    id: '1',
                    product,
                    quantity: 2,
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('cartStateChanged'));
        });

        it('should add new item to existing cart', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 1,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            const newProduct = {
                id: 2,
                name: 'Product 2',
                price: 200,
                description: 'Test description',
                imageUrl: 'test2.jpg',
            };

            cartService.addToCart(newProduct, 3);

            const expectedCart = [
                ...existingCart,
                {
                    id: '2',
                    product: newProduct,
                    quantity: 3,
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
        });

        it('should update quantity if item already exists', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            const product = {
                id: 1,
                name: 'Product 1',
                price: 100,
                description: '',
                imageUrl: '',
            };

            cartService.addToCart(product, 3);

            const expectedCart = [
                {
                    id: '1',
                    product,
                    quantity: 5, // 2 + 3
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
        });
    });

    describe('removeFromCart', () => {
        it('should remove item from cart', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 1,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            cartService.removeFromCart('1');

            const expectedCart = [
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 1,
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('cartStateChanged'));
        });

        it('should handle removing non-existent item', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            cartService.removeFromCart('999');

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(existingCart)
            );
        });
    });

    describe('updateCartItemQuantity', () => {
        it('should update item quantity', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            cartService.updateCartItemQuantity('1', 5);

            const expectedCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 5,
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('cartStateChanged'));
        });

        it('should remove item if quantity is 0 or negative', () => {
            const existingCart = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 1,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(existingCart));

            cartService.updateCartItemQuantity('1', 0);

            const expectedCart = [
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 1,
                },
            ];

            expect(localStorageMock.setItem).toHaveBeenCalledWith(
                'cart_items',
                JSON.stringify(expectedCart)
            );
        });
    });

    describe('getCartItems', () => {
        it('should return cart items from localStorage', () => {
            const cartItems = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
            ];
            localStorageMock.getItem.mockReturnValue(JSON.stringify(cartItems));

            const result = cartService.getCartItems();

            expect(result).toEqual(cartItems);
            expect(localStorageMock.getItem).toHaveBeenCalledWith('cart_items');
        });

        it('should return empty array when no cart exists', () => {
            localStorageMock.getItem.mockReturnValue(null);

            const result = cartService.getCartItems();

            expect(result).toEqual([]);
        });

        it('should return empty array when cart data is invalid', () => {
            localStorageMock.getItem.mockReturnValue('invalid json');

            const result = cartService.getCartItems();

            expect(result).toEqual([]);
        });
    });

    describe('getTotalItems', () => {
        it('should return total quantity of all items', () => {
            const cartItems = [
                {
                    id: '1',
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 3,
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
                    product: {
                        id: 1,
                        name: 'Product 1',
                        price: 100,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 2,
                },
                {
                    id: '2',
                    product: {
                        id: 2,
                        name: 'Product 2',
                        price: 200,
                        description: '',
                        imageUrl: '',
                    },
                    quantity: 3,
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

            expect(localStorageMock.removeItem).toHaveBeenCalledWith('cart_items');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('cartStateChanged'));
        });
    });
});
