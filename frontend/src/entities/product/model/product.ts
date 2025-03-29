import { NumericId } from '@/shared/types/numeric-id';

export type Product = {
    id: NumericId;
    name: string;
    price: number;
    description: string;
    imageUrl: string;
};
