import { describe, expect, it } from '@jest/globals';
import { preparePrice } from '@/shared/utils/prepare-price';

const expected1000Rub = '1 000 ₽';
const expected999and99Rub = '999,99 ₽';
const expected0and01Rub = '0,01 ₽';
const expected0Rub = '0 ₽';
const expectedMinus100Rub = '-100 ₽';

describe('preparePrice', () => {
    it('parses 1000 RUB for 1000', () => {
        const preparedPrice = preparePrice(1000);
        expect(preparedPrice).toBe(expected1000Rub);
    });

    it('parses 999.99 RUB for 999.99', () => {
        const preparedPrice = preparePrice(999.99);
        expect(preparedPrice).toBe(expected999and99Rub);
    });

    it('parses 0.01 RUB', () => {
        const preparedPrice = preparePrice(0.01);
        expect(preparedPrice).toBe(expected0and01Rub);
    });

    it('parses 0 RUB', () => {
        const preparedPrice = preparePrice(0);
        expect(preparedPrice).toBe(expected0Rub);
    });

    it('parses -100 RUB', () => {
        const preparedPrice = preparePrice(-100);
        expect(preparedPrice).toBe(expectedMinus100Rub);
    });
});
