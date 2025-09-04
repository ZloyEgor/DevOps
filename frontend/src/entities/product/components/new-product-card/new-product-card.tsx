'use client';
import React, { FC, useState } from 'react';
import { Props } from '@/shared/utils';
import { Product, productService } from '@/entities/product';
import { authService } from '@/entities/auth';
import styles from './styles.module.scss';

export type NewProductCardProps = Props<{
    catalogId: number;
    onCancel: () => void;
    onCreate: (product: Product) => void;
}>;

export const NewProductCard: FC<NewProductCardProps> = ({ catalogId, onCancel, onCreate }) => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [price, setPrice] = useState('');
    const [pictureUrl, setPictureUrl] = useState('');
    const [isCreating, setIsCreating] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    const handleCreate = async () => {
        if (!name.trim() || !description.trim() || !price.trim()) {
            alert('Please fill in all required fields');
            return;
        }

        const priceNumber = parseFloat(price);
        if (isNaN(priceNumber) || priceNumber <= 0) {
            alert('Please enter a valid price');
            return;
        }

        setIsCreating(true);
        try {
            const newProduct = await productService.createProduct({
                name: name.trim(),
                description: description.trim(),
                price: priceNumber,
                pictureUrl: pictureUrl.trim() || '/shared/mock/bouquet.jpg', // Default image
                catalogId: catalogId,
            });

            // Show success popup
            setShowSuccess(true);
            setTimeout(() => {
                setShowSuccess(false);
                onCreate(newProduct);
            }, 1000);

        } catch (error) {
            console.error('Failed to create product:', error);
            const errorMessage =
                error instanceof Error
                    ? error.message
                    : 'Failed to create product. Please try again.';
            alert(errorMessage);
        } finally {
            setIsCreating(false);
        }
    };

    const handleCancel = () => {
        onCancel();
    };

    // Don't render if user is not admin
    if (!authService.isAdmin()) {
        return null;
    }

    return (
        <div className={styles.card}>
            <div className={styles.imageContainer}>
                <div className={styles.imagePlaceholder}>
                    {pictureUrl ? (
                        <img src={pictureUrl} alt="Preview" className={styles.image} />
                    ) : (
                        <span className={styles.placeholderText}>Image Preview</span>
                    )}
                </div>
            </div>

            <div className={styles.content}>
                <div className={styles.header}>
                    <input
                        type="text"
                        placeholder="Product name*"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className={styles.nameInput}
                        disabled={isCreating}
                    />
                </div>

                <textarea
                    placeholder="Product description*"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className={styles.descriptionInput}
                    disabled={isCreating}
                    rows={3}
                />

                <div className={styles.inputRow}>
                    <input
                        type="number"
                        placeholder="Price*"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        className={styles.priceInput}
                        disabled={isCreating}
                        min="0"
                        step="0.01"
                    />
                    <input
                        type="url"
                        placeholder="Image URL (optional)"
                        value={pictureUrl}
                        onChange={(e) => setPictureUrl(e.target.value)}
                        className={styles.urlInput}
                        disabled={isCreating}
                    />
                </div>

                <div className={styles.footer}>
                    <small className={styles.hint}>* Required fields</small>
                </div>

                <div className={styles.actions}>
                    <button
                        onClick={handleCreate}
                        className={styles.saveButton}
                        disabled={isCreating}
                        title="Create product"
                    >
                        {isCreating ? 'Creating...' : '✓ Create'}
                    </button>
                    <button
                        onClick={handleCancel}
                        className={styles.cancelButton}
                        disabled={isCreating}
                        title="Cancel"
                    >
                        ✕ Cancel
                    </button>
                </div>
            </div>

            {/* Success Popup */}
            {showSuccess && (
                <div className={styles.successPopup}>
                    <div className={styles.successContent}>
                        <span className={styles.successIcon}>✓</span>
                        <span>Product created successfully!</span>
                    </div>
                </div>
            )}
        </div>
    );
};
