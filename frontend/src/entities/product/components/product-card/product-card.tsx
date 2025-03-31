import { Props } from '@/shared/utils';
import { FC, HTMLProps } from 'react';
import styles from './styles.module.scss';
import clsx from 'clsx';
import { preparePrice } from '@/shared/utils/prepare-price';
import { Product } from '@/entities/product';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';
import Image from 'next/image';

export type CatalogItemCardProps = Props<
    { item: Product; withDescription?: boolean },
    true,
    HTMLProps<HTMLDivElement>
>;

export const ProductCard: FC<CatalogItemCardProps> = ({
    item,
    className,
    children,
    withDescription = true,
    ...rest
}) => {
    const { name, price, description } = item;

    return (
        <div className={clsx(styles.card, className)} {...rest}>
            <Image width={256} height={256} className={styles.image} src={mockPhoto} alt={name} />
            <div className={styles.descriptionContainer}>
                <span className={styles.title}>{name}</span>
                {withDescription ? <span className={styles.description}>{description}</span> : null}
                <span className={styles.price}>{preparePrice(price)}</span>
                {children}
            </div>
        </div>
    );
};
