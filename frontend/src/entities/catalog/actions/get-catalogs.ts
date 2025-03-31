'use server';
import { catalogService } from '@/entities/catalog';

export const getCatalogs = async () => {
    return { catalogs: await catalogService.getCatalogs() };
};
