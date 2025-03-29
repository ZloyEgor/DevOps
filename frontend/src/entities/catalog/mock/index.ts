import { CatalogEntry } from '@/entities/catalog';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';

export const catalogEntryMock: CatalogEntry = {
    id: 1,
    name: 'Весенняя коллекция',
    description: 'Подборка букетов, вдохновлённая весной: нежность, свежесть и аромат.',
    items: [
        {
            id: 101,
            name: 'Рассвет',
            price: 2890,
            description:
                'Букет из тюльпанов и пионов в пастельных тонах. Идеален для утреннего подарка.',
            imageUrl: mockPhoto.src,
        },
        {
            id: 102,
            name: 'Солнечное настроение',
            price: 2390,
            description: 'Яркий микс из подсолнухов, ромашек и гипсофилы. Напоминает о лете.',
            imageUrl: mockPhoto.src,
        },
        {
            id: 103,
            name: 'Белая элегантность',
            price: 3190,
            description:
                'Элегантная композиция из белых роз и лилий. Классика, которая всегда уместна.',
            imageUrl: mockPhoto.src,
        },
    ],
};

export const catalogMock: CatalogEntry[] = [catalogEntryMock];
