import { render, screen } from '@testing-library/react';
import { Header } from './header';
import '@testing-library/jest-dom';

// Mock next/navigation
jest.mock('next/navigation', () => ({
    useRouter: () => ({
        push: jest.fn(),
    }),
}));

// Mock services with simple implementations
jest.mock('@/entities/auth', () => ({
    authService: {
        isAuthenticated: () => false,
        getUser: () => null,
        logout: jest.fn().mockResolvedValue(undefined),
    },
}));

jest.mock('@/entities/cart', () => ({
    cartService: {
        getTotalItems: () => 0,
    },
}));

describe('Header', () => {
    beforeEach(() => {
        // Reset event listeners
        window.removeEventListener = jest.fn();
        window.addEventListener = jest.fn();
    });

    it('renders logo and navigation', () => {
        render(<Header />);

        expect(screen.getByText('Магазин цветов "Цвет Очей"')).toBeInTheDocument();
        expect(screen.getByText('Войти')).toBeInTheDocument();
    });

    it('applies custom className to logo', () => {
        const { container } = render(<Header className="custom-header" />);

        // The className is applied to the Logo component inside the header
        expect(container.querySelector('.custom-header')).toBeInTheDocument();
    });

    it('sets up event listeners on mount', () => {
        render(<Header />);

        expect(window.addEventListener).toHaveBeenCalledWith('focus', expect.any(Function));
        expect(window.addEventListener).toHaveBeenCalledWith('authStateChanged', expect.any(Function));
        expect(window.addEventListener).toHaveBeenCalledWith('cartStateChanged', expect.any(Function));
    });

    it('cleans up event listeners on unmount', () => {
        const { unmount } = render(<Header />);
        unmount();

        expect(window.removeEventListener).toHaveBeenCalledWith('focus', expect.any(Function));
        expect(window.removeEventListener).toHaveBeenCalledWith('authStateChanged', expect.any(Function));
        expect(window.removeEventListener).toHaveBeenCalledWith('cartStateChanged', expect.any(Function));
    });
});