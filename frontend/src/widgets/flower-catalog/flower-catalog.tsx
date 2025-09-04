'use client';
import React, { FC, useState, useEffect } from 'react';
import { CatalogEntry } from '@/entities/catalog';
import { Props } from '@/shared/utils';
import styles from './flower-catalog.module.scss';
import { EditableProductCard, Product, NewProductCard } from '@/entities/product';
import { authService } from '@/entities/auth';

export type FlowerCatalogProps = Props<{
    catalog: CatalogEntry;
}>;
export const FlowerCatalog: FC<FlowerCatalogProps> = ({ catalog }) => {
    const { productDtos: products, name, id: catalogId } = catalog;
    const [isOpen, setIsOpen] = useState(true);
    const [catalogProducts, setCatalogProducts] = useState<Product[]>(products);
    const [isCreating, setIsCreating] = useState(false);
    const [isAdmin, setIsAdmin] = useState(false);

    // Track admin status
    useEffect(() => {
        const updateAdminStatus = () => {
            setIsAdmin(authService.isAdmin());
        };

        updateAdminStatus();

        // Listen for auth state changes
        const handleAuthChange = () => {
            updateAdminStatus();
        };

        window.addEventListener('authStateChanged', handleAuthChange);
        return () => {
            window.removeEventListener('authStateChanged', handleAuthChange);
        };
    }, []);

    const onClick: React.MouseEventHandler = () => {
        setIsOpen((prev) => !prev);
    };

    const onListClick: React.MouseEventHandler = (event) => {
        event.stopPropagation();
    };

    const handleProductUpdate = (updatedProduct: Product) => {
        setCatalogProducts((prev) =>
            prev.map((product) => (product.id === updatedProduct.id ? updatedProduct : product))
        );
    };

    const handleProductDelete = (deletedProductId: number) => {
        setCatalogProducts((prev) => prev.filter((product) => product.id !== deletedProductId));
    };

    const handleStartCreate = () => {
        setIsCreating(true);
    };

    const handleCancelCreate = () => {
        setIsCreating(false);
    };

    const handleProductCreate = (newProduct: Product) => {
        setCatalogProducts((prev) => [...prev, newProduct]);
        setIsCreating(false);
    };

    return (
        <details className={styles.container} open={isOpen} onClick={onClick}>
            <summary className={styles.title}>{name}</summary>
            <div className={styles.list} onClick={onListClick}>
                {catalogProducts.map((item) => (
                    <EditableProductCard
                        key={item.id}
                        item={item}
                        onUpdate={handleProductUpdate}
                        onDelete={handleProductDelete}
                    />
                ))}

                {/* New Product Card */}
                {isCreating && (
                    <NewProductCard
                        catalogId={catalogId}
                        onCancel={handleCancelCreate}
                        onCreate={handleProductCreate}
                    />
                )}

                {/* Create Button */}
                {isAdmin && !isCreating && (
                    <div className={styles.createButtonContainer}>
                        <button
                            onClick={handleStartCreate}
                            className={styles.createButton}
                            title="Add new product to this catalog"
                        >
                            <span className={styles.createIcon}>+</span>
                            <span className={styles.createText}>Add New Product</span>
                        </button>
                    </div>
                )}
            </div>
        </details>
    );
};
