'use server';
import { catalogServiceMock } from '@/entities/catalog/api/catalog-service.mock';

export const getCatalog = async () => {
    return await catalogServiceMock.getCatalog();
};
