'use client';
import { Props } from '@/shared/utils';
import React, { FC, HTMLProps, useState, useEffect } from 'react';
import { authService, User } from '@/entities/auth';
import { cartService, LocalCartItem } from '@/entities/cart/api/cart-service';
import { productService } from '@/entities/product/api/product-service';
import styles from './styles.module.scss';
import clsx from 'clsx';
import { preparePrice } from '@/shared/utils/prepare-price';
import { Product } from '@/entities/product';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';
import Image from 'next/image';

export type EditableProductCardProps = Props<
    {
        item: Product;
        withDescription?: boolean;
        showAddToCart?: boolean;
        onUpdate?: (product: Product) => void;
        onDelete?: (productId: number) => void;
    },
    true,
    HTMLProps<HTMLDivElement>
>;

export const EditableProductCard: FC<EditableProductCardProps> = ({
    item,
    className,
    children,
    withDescription = true,
    showAddToCart = true,
    onUpdate,
    onDelete,
    ...rest
}) => {
    const [isEditing, setIsEditing] = useState(false);
    const [isAdding, setIsAdding] = useState(false);
    const [isAdded, setIsAdded] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    // Cart state
    const [cartItem, setCartItem] = useState<LocalCartItem | undefined>();
    const [cartQuantity, setCartQuantity] = useState(0);

    // Editable fields
    const [editName, setEditName] = useState(item.name);
    const [editDescription, setEditDescription] = useState(item.description);
    const [editPrice, setEditPrice] = useState(item.price.toString());

    const user: User | null = authService.getUser();
    const isAdmin = user?.userRole === 'ADMIN';

    // Track cart state
    useEffect(() => {
        const updateCartState = () => {
            const itemInCart = cartService.getCartItemByProductId(item.id);
            setCartItem(itemInCart);
            setCartQuantity(itemInCart?.quantity || 0);
        };

        updateCartState();

        // Listen for cart changes
        const handleCartChange = () => updateCartState();
        window.addEventListener('cartStateChanged', handleCartChange);

        return () => {
            window.removeEventListener('cartStateChanged', handleCartChange);
        };
    }, [item.id]);

    const handleAddToCart = async (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (!authService.isAuthenticated()) {
            window.location.href = '/login';
            return;
        }

        setIsAdding(true);
        try {
            cartService.addProductToCart(item);
            setIsAdded(true);
            setTimeout(() => setIsAdded(false), 2000);
        } catch (error) {
            console.error('Failed to add to cart:', error);
            alert('Failed to add item to cart');
        } finally {
            setIsAdding(false);
        }
    };

    const handleEdit = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();
        setIsEditing(true);
    };

    const handleCancel = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();
        // Reset to original values
        setEditName(item.name);
        setEditDescription(item.description);
        setEditPrice(item.price.toString());
        setIsEditing(false);
    };

    const handleQuantityIncrease = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (cartItem) {
            cartService.updateCartItemQuantity(cartItem.id, cartQuantity + 1);
        } else {
            cartService.addProductToCart(item);
        }
    };

    const handleQuantityDecrease = (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (cartItem && cartQuantity > 1) {
            cartService.updateCartItemQuantity(cartItem.id, cartQuantity - 1);
        } else if (cartItem) {
            cartService.removeFromCart(cartItem.id);
        }
    };

    const handleQuantityChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        e.stopPropagation();

        const newQuantity = parseInt(e.target.value) || 0;
        if (cartItem && newQuantity > 0) {
            cartService.updateCartItemQuantity(cartItem.id, newQuantity);
        } else if (cartItem && newQuantity === 0) {
            cartService.removeFromCart(cartItem.id);
        }
    };

    const handleSave = async (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        const priceNumber = parseFloat(editPrice);
        if (isNaN(priceNumber) || priceNumber <= 0) {
            alert('Please enter a valid price');
            return;
        }

        if (!editName.trim()) {
            alert('Product name cannot be empty');
            return;
        }

        setIsSaving(true);
        try {
            const updatedProduct = await productService.updateProduct(item, {
                name: editName.trim(),
                description: editDescription.trim(),
                price: priceNumber,
            });

            setIsEditing(false);
            setShowSuccess(true);
            setTimeout(() => setShowSuccess(false), 3000);

            // Call parent update handler if provided
            if (onUpdate) {
                onUpdate(updatedProduct);
            }
        } catch (error) {
            console.error('Failed to update product:', error);
            alert('Failed to update product. Please try again.');
        } finally {
            setIsSaving(false);
        }
    };

    const handleDelete = async () => {
        const confirmed = window.confirm(
            `Are you sure you want to delete "${item.name}"?\n\nThis action cannot be undone.`
        );

        if (!confirmed) {
            return;
        }

        setIsDeleting(true);
        try {
            await productService.deleteProduct(item.id);

            if (onDelete) {
                onDelete(item.id);
            }
        } catch (error) {
            console.error('Failed to delete product:', error);
            const errorMessage =
                error instanceof Error
                    ? error.message
                    : 'Failed to delete product. Please try again.';
            alert(errorMessage);
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div className={clsx(styles.card, className)} {...rest}>
            <Image
                width={256}
                height={256}
                className={styles.image}
                src={mockPhoto}
                alt={editName}
            />
            <div className={styles.descriptionContainer}>
                {isEditing ? (
                    <>
                        <input
                            type="text"
                            value={editName}
                            onChange={(e) => setEditName(e.target.value)}
                            className={styles.editInput}
                            placeholder="Product name"
                        />
                        {withDescription && (
                            <textarea
                                value={editDescription}
                                onChange={(e) => setEditDescription(e.target.value)}
                                className={styles.editTextarea}
                                placeholder="Product description"
                                rows={3}
                            />
                        )}
                        <input
                            type="number"
                            value={editPrice}
                            onChange={(e) => setEditPrice(e.target.value)}
                            className={styles.editInput}
                            placeholder="Price"
                            min="0"
                            step="0.01"
                        />
                        <div className={styles.editButtons}>
                            <button
                                onClick={handleSave}
                                disabled={isSaving}
                                className={styles.saveButton}
                            >
                                {isSaving ? 'Saving...' : 'Save'}
                            </button>
                            <button
                                onClick={handleCancel}
                                disabled={isSaving}
                                className={styles.cancelButton}
                            >
                                Cancel
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <div className={styles.titleContainer}>
                            <span className={styles.title}>{item.name}</span>
                            {isAdmin && (
                                <div className={styles.adminActions}>
                                    <button
                                        onClick={handleEdit}
                                        className={styles.editButton}
                                        title="Edit product"
                                        disabled={isDeleting}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        onClick={handleDelete}
                                        className={styles.deleteButton}
                                        title="Delete product"
                                        disabled={isDeleting}
                                    >
                                        {isDeleting ? 'Deleting...' : 'Delete'}
                                    </button>
                                </div>
                            )}
                        </div>
                        {withDescription && (
                            <span className={styles.description}>{item.description}</span>
                        )}
                        <span className={styles.price}>{preparePrice(item.price)}</span>
                    </>
                )}
                {children}
            </div>

            {/* Cart Controls at Bottom */}
            {showAddToCart && !isEditing && (
                <div className={styles.cartSection}>
                    {cartQuantity > 0 ? (
                        <div className={styles.quantityControls}>
                            <button
                                onClick={handleQuantityDecrease}
                                className={styles.quantityButton}
                            >
                                -
                            </button>
                            <input
                                type="number"
                                value={cartQuantity}
                                onChange={handleQuantityChange}
                                className={styles.quantityInput}
                                min="0"
                            />
                            <button
                                onClick={handleQuantityIncrease}
                                className={styles.quantityButton}
                            >
                                +
                            </button>
                        </div>
                    ) : (
                        <button
                            onClick={handleAddToCart}
                            disabled={isAdding}
                            className={clsx(styles.addToCartButton, {
                                [styles.adding]: isAdding,
                                [styles.added]: isAdded,
                            })}
                        >
                            {isAdding ? 'Добавляем...' : isAdded ? 'Добавлено!' : 'В корзину'}
                        </button>
                    )}
                </div>
            )}

            {showSuccess && (
                <div className={styles.successPopup}>Product updated successfully!</div>
            )}
        </div>
    );
};
