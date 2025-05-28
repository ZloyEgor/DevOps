import { CatalogEntry } from '../model/catalog';
import * as process from 'node:process';

const baseUrl = process.env.BACKEND_URL;

export const catalogService = {
    getCatalogs: (): Promise<CatalogEntry[]> =>
        fetch(`${baseUrl}/cvet-ochey/api/v1/catalog`, { cache: 'no-store' })
            .then((r) => r.json())
            .catch(() => {
                return [];
            }),
};
