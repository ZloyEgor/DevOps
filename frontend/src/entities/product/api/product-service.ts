import { API_CONFIG } from '@/shared/config/api';
import { Product } from '../model/product';
import { authService } from '@/entities/auth';

class ProductService {
    async updateProduct(currentProduct: Product, updates: Partial<Product>): Promise<Product> {
        const token = authService.getToken();
        if (!token) {
            throw new Error('Authentication required');
        }

        // Merge current product data with updates to preserve all fields
        const fullProductData = {
            ...currentProduct,
            ...updates,
            // Ensure we always preserve these critical fields
            id: currentProduct.id,
            pictureUrl: currentProduct.pictureUrl,
            catalogId: currentProduct.catalogId,
        };

        const response = await fetch(
            `${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.PRODUCTS}/${currentProduct.id}`,
            {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(fullProductData),
            }
        );

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Unauthorized: Admin access required');
            }
            throw new Error('Failed to update product');
        }

        return response.json();
    }

    async getProduct(productId: number): Promise<Product> {
        const response = await fetch(
            `${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.PRODUCTS}/${productId}`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );

        if (!response.ok) {
            throw new Error('Failed to fetch product');
        }

        return response.json();
    }
}

export const productService = new ProductService();
