import { FC } from 'react';
import { getCatalog } from '@/entities/catalog';
import { ProductCard } from '@/entities/product';
import styles from './catalog.module.scss';

const Catalog: FC = async () => {
    const catalog = await getCatalog();
    if (!catalog.at(0)) return 'Каталог пуст';
    return (
        <>
            <p className={styles.title}>Коллекция букетов</p>
            <div className={styles.list}>
                {catalog.at(0)?.items.map((item) => <ProductCard key={item.id} item={item} />)}
            </div>
        </>
    );
};

export default Catalog;
