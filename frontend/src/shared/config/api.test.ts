import { API_CONFIG } from './api';

describe('API Configuration', () => {
    it('should have correct base URL', () => {
        expect(API_CONFIG.BASE_URL).toBe('http://localhost:8080');
    });

    it('should have correct API prefix', () => {
        expect(API_CONFIG.API_PREFIX).toBe('/cvet-ochey/api/v1');
    });

    it('should have correct full API URL', () => {
        expect(API_CONFIG.FULL_API_URL).toBe('http://localhost:8080/cvet-ochey/api/v1');
    });

    it('should have auth endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.AUTH).toBeDefined();
        expect(API_CONFIG.ENDPOINTS.AUTH.LOGIN).toBe('/auth/login');
        expect(API_CONFIG.ENDPOINTS.AUTH.REGISTER).toBe('/auth/register');
        expect(API_CONFIG.ENDPOINTS.AUTH.LOGOUT).toBe('/auth/logout');
    });

    it('should have product endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.PRODUCTS).toBe('/products');
    });

    it('should have catalog endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.CATALOG).toBe('/catalog');
    });

    it('should have clients endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.CLIENTS).toBe('/clients');
    });

    it('should have orders endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.ORDERS).toBe('/orders');
    });

    it('should have cart endpoints', () => {
        expect(API_CONFIG.ENDPOINTS.CART).toBe('/cart');
    });
});
