import { render, screen } from '@testing-library/react';
import Home from './page';
import '@testing-library/jest-dom';

describe('Home Page', () => {
    it('renders catalog link', () => {
        render(<Home />);

        const catalogLink = screen.getByRole('link', { name: 'Перейти в каталог цветов' });
        expect(catalogLink).toBeInTheDocument();
        expect(catalogLink).toHaveAttribute('href', '/catalog');
    });

    it('renders the correct text', () => {
        render(<Home />);

        expect(screen.getByText('Перейти в каталог цветов')).toBeInTheDocument();
    });
});
