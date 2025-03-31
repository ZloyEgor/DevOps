import { CatalogEntry } from '@/entities/catalog';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';

export const catalogEntryMock: CatalogEntry = {
    id: 1,
    name: 'Весенняя коллекция',
    catalogType: 'FLOWERS',
    description: 'Подборка букетов, вдохновлённая весной: нежность, свежесть и аромат.',
    productDtos: [
        {
            id: 101,
            catalogId: 1,
            name: 'Рассвет',
            price: 2890,
            description:
                'Букет из тюльпанов и пионов в пастельных тонах. Идеален для утреннего подарка.',
            pictureUrl: mockPhoto.src,
        },
        {
            id: 102,
            catalogId: 1,
            name: 'Солнечное настроение',
            price: 2390,
            description: 'Яркий микс из подсолнухов, ромашек и гипсофилы. Напоминает о лете.',
            pictureUrl: mockPhoto.src,
        },
        {
            id: 103,
            catalogId: 1,
            name: 'Белая элегантность',
            price: 3190,
            description:
                'Элегантная композиция из белых роз и лилий. Классика, которая всегда уместна.',
            pictureUrl: mockPhoto.src,
        },
    ],
};

export const catalogMock: CatalogEntry[] = [catalogEntryMock];
