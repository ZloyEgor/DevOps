'use client';
import { Props } from '@/shared/utils';
import React, { FC, HTMLProps, useState } from 'react';
import { authService } from '@/entities/auth';
import { cartService } from '@/entities/cart';
import styles from './styles.module.scss';
import clsx from 'clsx';
import { preparePrice } from '@/shared/utils/prepare-price';
import { Product } from '@/entities/product';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';
import Image from 'next/image';

export type CatalogItemCardProps = Props<
    { item: Product; withDescription?: boolean; showAddToCart?: boolean },
    true,
    HTMLProps<HTMLDivElement>
>;

export const ProductCard: FC<CatalogItemCardProps> = ({
    item,
    className,
    children,
    withDescription = true,
    showAddToCart = true,
    ...rest
}) => {
    const { name, price, description } = item;
    const [isAdding, setIsAdding] = useState(false);
    const [isAdded, setIsAdded] = useState(false);

    const handleAddToCart = async (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (!authService.isAuthenticated()) {
            // Redirect to login or show login modal
            window.location.href = '/login';
            return;
        }

        setIsAdding(true);
        try {
            cartService.addProductToCart(item);
            setIsAdded(true);
            setTimeout(() => setIsAdded(false), 2000); // Reset after 2 seconds
        } catch (error) {
            console.error('Failed to add to cart:', error);
            alert('Failed to add item to cart');
        } finally {
            setIsAdding(false);
        }
    };

    return (
        <div className={clsx(styles.card, className)} {...rest}>
            <Image width={256} height={256} className={styles.image} src={mockPhoto} alt={name} />
            <div className={styles.descriptionContainer}>
                <span className={styles.title}>{name}</span>
                {withDescription ? <span className={styles.description}>{description}</span> : null}
                <span className={styles.price}>{preparePrice(price)}</span>
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
                {children}
            </div>
        </div>
    );
};
