import { render, screen } from '@testing-library/react';
import { ProductCard } from './index';
import { Product } from '@/entities/product';
import '@testing-library/jest-dom';
import { describe, expect, it } from '@jest/globals';
import mockPhoto from '@/shared/assets/mock/bouquet.jpg';

const mockProduct: Product = {
    id: 1,
    name: 'Рассвет',
    description: 'Нежный букет с пионами и тюльпанами.',
    price: 2890,
    imageUrl: mockPhoto.src,
};

describe('<ProductCard />', () => {
    it('renders product name, description, price, and image', () => {
        render(<ProductCard item={mockProduct} />);

        expect(screen.getByText(mockProduct.name)).toBeInTheDocument();
        expect(screen.getByText(mockProduct.description)).toBeInTheDocument();
        expect(screen.getByText('2 890 ₽')).toBeInTheDocument(); // форматированный preparePrice

        const image = screen.getByRole('img');
        expect(image).toHaveAttribute('alt', mockProduct.name);
    });

    it('hides description if withDescription is false', () => {
        render(<ProductCard item={mockProduct} withDescription={false} />);

        expect(screen.queryByText(mockProduct.description)).not.toBeInTheDocument();
        expect(screen.getByText(mockProduct.name)).toBeInTheDocument(); // still visible
    });

    it('renders children inside the card', () => {
        render(
            <ProductCard item={mockProduct}>
                <button>Добавить в корзину</button>
            </ProductCard>
        );

        expect(screen.getByText('Добавить в корзину')).toBeInTheDocument();
    });

    it('applies passed className', () => {
        const { container } = render(<ProductCard item={mockProduct} className="custom-class" />);

        expect(container.firstChild).toHaveClass('custom-class');
    });
});
