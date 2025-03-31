import { NumericId } from '@/shared/types/numeric-id';
import { Product } from '@/entities/product';

export type CatalogEntry = {
    id: NumericId;
    name: string;
    description: string;
    catalogType: 'FLOWERS';
    productDtos: Product[];
};
