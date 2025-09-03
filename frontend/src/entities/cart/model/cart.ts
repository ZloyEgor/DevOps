import { NumericId } from '@/shared/types/numeric-id';
import { Product } from '@/entities/product';

export interface CartItem {
    id: NumericId;
    clientId: NumericId;
    product: Product;
    quantity: number;
    totalPrice: number;
}
