export const API_CONFIG = {
    BASE_URL:
        process.env.NEXT_PUBLIC_API_URL ||
        (typeof window !== 'undefined' && window.location.hostname === 'local.cvetochey.ru'
            ? 'http://localhost:8080'
            : 'http://localhost:8080'),
    API_PREFIX: '/cvet-ochey/api/v1',

    get FULL_API_URL() {
        return `${this.BASE_URL}${this.API_PREFIX}`;
    },

    ENDPOINTS: {
        CATALOG: '/catalog',
        PRODUCTS: '/products',
        CLIENTS: '/clients',
        ORDERS: '/orders',
        CART: '/cart',
        AUTH: {
            LOGIN: '/auth/login',
            LOGOUT: '/auth/logout',
            REGISTER: '/auth/register',
            REFRESH: '/auth/refresh',
        },
    },
} as const;

export type ApiEndpoint = keyof typeof API_CONFIG.ENDPOINTS;
