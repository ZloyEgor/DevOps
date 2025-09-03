import { API_CONFIG } from '@/shared/config/api';
import { CatalogEntry } from '../model/catalog';

export const catalogService = {
    getCatalogs: (): Promise<CatalogEntry[]> =>
        fetch(`${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.CATALOG}`, { 
            cache: 'no-store',
            headers: {
                'Content-Type': 'application/json',
            },
        }).then((r) => {
            if (!r.ok) {
                throw new Error(`Failed to fetch catalogs: ${r.status}`);
            }
            return r.json();
        }),

    getCatalogById: (id: number): Promise<CatalogEntry> =>
        fetch(`${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.CATALOG}/${id}`, {
            headers: {
                'Content-Type': 'application/json',
            },
        }).then((r) => {
            if (!r.ok) {
                throw new Error(`Failed to fetch catalog: ${r.status}`);
            }
            return r.json();
        }),
};
