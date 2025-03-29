export const preparePrice = (price: number): string => {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
    })
        .format(price)
        .replace(/[.,]00/, '');
};
