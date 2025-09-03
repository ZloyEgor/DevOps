'use client';
import { Props } from '@/shared/utils';
import { FC, HTMLProps, useState } from 'react';
import { authService, User } from '@/entities/auth';
import { cartService } from '@/entities/cart';
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
    ...rest
}) => {
    const [isEditing, setIsEditing] = useState(false);
    const [isAdding, setIsAdding] = useState(false);
    const [isAdded, setIsAdded] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    // Editable fields
    const [editName, setEditName] = useState(item.name);
    const [editDescription, setEditDescription] = useState(item.description);
    const [editPrice, setEditPrice] = useState(item.price.toString());

    const user: User | null = authService.getUser();
    const isAdmin = user?.userRole === 'ADMIN';

    const handleAddToCart = async (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (!authService.isAuthenticated()) {
            window.location.href = '/login';
            return;
        }

        setIsAdding(true);
        try {
            await cartService.addToCart(item.id);
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
                                <button
                                    onClick={handleEdit}
                                    className={styles.editButton}
                                    title="Edit product"
                                >
                                    Edit
                                </button>
                            )}
                        </div>
                        {withDescription && (
                            <span className={styles.description}>{item.description}</span>
                        )}
                        <span className={styles.price}>{preparePrice(item.price)}</span>
                        {showAddToCart && (
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
                    </>
                )}
                {children}
            </div>
            {showSuccess && (
                <div className={styles.successPopup}>Product updated successfully!</div>
            )}
        </div>
    );
};
