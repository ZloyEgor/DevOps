'use client';
import React, { FC, useState } from 'react';
import { CatalogEntry } from '@/entities/catalog';
import { Props } from '@/shared/utils';
import styles from './flower-catalog.module.scss';
import { ProductCard } from '@/entities/product';

export type FlowerCatalogProps = Props<{
    catalog: CatalogEntry;
}>;
export const FlowerCatalog: FC<FlowerCatalogProps> = ({ catalog }) => {
    const { productDtos: products, name } = catalog;
    const [isOpen, setIsOpen] = useState(true);

    const onClick: React.MouseEventHandler = () => {
        setIsOpen((prev) => !prev);
    };

    const onListClick: React.MouseEventHandler = (event) => {
        event.stopPropagation();
    };

    return (
        <details className={styles.container} open={isOpen} onClick={onClick}>
            <summary className={styles.title}>{name}</summary>
            <div className={styles.list} onClick={onListClick}>
                {products.map((item) => (
                    <ProductCard key={item.id} item={item} />
                ))}
            </div>
        </details>
    );
};
