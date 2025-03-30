import { catalogMock } from '@/entities/catalog/mock';

export const catalogServiceMock = {
    getCatalog: () => {
        return Promise.resolve(catalogMock);
    },
};
