import { FC } from 'react';
import { getCatalogs } from '@/entities/catalog';
import styles from './catalog.module.scss';
import { FlowerCatalog } from '@/widgets/flower-catalog';

const Catalog: FC = async () => {
    const { catalogs } = await getCatalogs();
    if (!catalogs) return 'Каталог пуст';
    return (
        <>
            <p className={styles.title}>Коллекция букетов</p>
            <div className={styles.list}>
                {catalogs.map((item) => (
                    <FlowerCatalog key={item.id} catalog={item} />
                ))}
            </div>
        </>
    );
};

export default Catalog;
