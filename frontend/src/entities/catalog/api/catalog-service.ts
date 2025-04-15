import { CatalogEntry } from '../model/catalog';

export const catalogService = {
    getCatalogs: (): Promise<CatalogEntry[]> =>
        fetch('http://nesterrovv.ru:8080/cvet-ochey/api/v1/catalog', { cache: 'no-store' }).then(
            (r) => r.json()
        ),
};
