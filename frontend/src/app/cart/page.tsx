'use client';
import { FC, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/entities/auth';
import { cartService, LocalCartItem } from '@/entities/cart/api/cart-service';
import { preparePrice } from '@/shared/utils/prepare-price';
import styles from './cart.module.scss';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';
import Image from 'next/image';

const CartPage: FC = () => {
    const [cartItems, setCartItems] = useState<LocalCartItem[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const router = useRouter();

    useEffect(() => {
        // Check if user is authenticated
        if (!authService.isAuthenticated()) {
            router.push('/login');
            return;
        }

        // Load cart items
        const loadCartItems = () => {
            const items = cartService.getCartItems();
            setCartItems(items);
            setIsLoading(false);
        };

        loadCartItems();

        // Listen for cart changes
        const handleCartChange = () => loadCartItems();
        window.addEventListener('cartStateChanged', handleCartChange);

        return () => {
            window.removeEventListener('cartStateChanged', handleCartChange);
        };
    }, [router]);

    const handleQuantityChange = (itemId: string, newQuantity: number) => {
        if (newQuantity <= 0) {
            cartService.removeFromCart(itemId);
        } else {
            cartService.updateCartItemQuantity(itemId, newQuantity);
        }
    };

    const handleRemoveItem = (itemId: string) => {
        cartService.removeFromCart(itemId);
    };

    const handleClearCart = () => {
        if (confirm('Are you sure you want to clear your cart?')) {
            cartService.clearCart();
        }
    };

    const totalPrice = cartService.getTotalPrice();
    const totalItems = cartService.getTotalItems();

    if (isLoading) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading your cart...</div>
            </div>
        );
    }

    if (cartItems.length === 0) {
        return (
            <div className={styles.container}>
                <div className={styles.header}>
                    <h1 className={styles.title}>Your Shopping Cart</h1>
                </div>
                <div className={styles.emptyCart}>
                    <p>Your cart is empty</p>
                    <button onClick={() => router.push('/catalog')} className={styles.shopButton}>
                        Continue Shopping
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Your Shopping Cart</h1>
                <div className={styles.summary}>
                    {totalItems} {totalItems === 1 ? 'item' : 'items'} • {preparePrice(totalPrice)}
                </div>
            </div>

            <div className={styles.cartContent}>
                <div className={styles.cartItems}>
                    {cartItems.map((item) => (
                        <div key={item.id} className={styles.cartItem}>
                            <Image
                                src={mockPhoto}
                                alt={item.product.name}
                                width={80}
                                height={80}
                                className={styles.productImage}
                            />
                            <div className={styles.productInfo}>
                                <h3 className={styles.productName}>{item.product.name}</h3>
                                <p className={styles.productDescription}>
                                    {item.product.description}
                                </p>
                                <div className={styles.productPrice}>
                                    {preparePrice(item.product.price)} each
                                </div>
                            </div>
                            <div className={styles.quantityControls}>
                                <button
                                    onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                                    className={styles.quantityButton}
                                >
                                    -
                                </button>
                                <input
                                    type="number"
                                    value={item.quantity}
                                    onChange={(e) =>
                                        handleQuantityChange(item.id, parseInt(e.target.value) || 0)
                                    }
                                    className={styles.quantityInput}
                                    min="0"
                                />
                                <button
                                    onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                                    className={styles.quantityButton}
                                >
                                    +
                                </button>
                            </div>
                            <div className={styles.itemTotal}>
                                {preparePrice(item.product.price * item.quantity)}
                            </div>
                            <button
                                onClick={() => handleRemoveItem(item.id)}
                                className={styles.removeButton}
                                title="Remove item"
                            >
                                Remove
                            </button>
                        </div>
                    ))}
                </div>

                <div className={styles.cartActions}>
                    <button onClick={handleClearCart} className={styles.clearButton}>
                        Clear Cart
                    </button>
                    <button
                        onClick={() => router.push('/catalog')}
                        className={styles.continueButton}
                    >
                        Continue Shopping
                    </button>
                    <button
                        className={styles.checkoutButton}
                        onClick={() => alert('Checkout functionality coming soon!')}
                    >
                        Checkout ({preparePrice(totalPrice)})
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
