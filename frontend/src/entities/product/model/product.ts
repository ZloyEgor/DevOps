import { NumericId } from '@/shared/types/numeric-id';

export type Product = {
    id: NumericId;
    catalogId: NumericId;
    description: string;
    name: string;
    price: number;
    pictureUrl: string;
};
