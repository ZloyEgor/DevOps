import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { useRouter } from 'next/navigation';
import { Header } from './header';
import { authService } from '@/entities/auth';
import { cartService } from '@/entities/cart';
import '@testing-library/jest-dom';

// Mock next/navigation
jest.mock('next/navigation', () => ({
    useRouter: jest.fn(),
}));

// Mock services
jest.mock('@/entities/auth', () => ({
    authService: {
        isAuthenticated: jest.fn(),
        getUser: jest.fn(),
        logout: jest.fn(),
    },
}));

jest.mock('@/entities/cart', () => ({
    cartService: {
        getTotalItems: jest.fn(),
    },
}));

describe('Header', () => {
    const mockPush = jest.fn();
    const mockAuthService = authService as jest.Mocked<typeof authService>;
    const mockCartService = cartService as jest.Mocked<typeof cartService>;

    beforeEach(() => {
        (useRouter as jest.Mock).mockReturnValue({
            push: mockPush,
        });
        jest.clearAllMocks();

        // Reset event listeners
        window.removeEventListener = jest.fn();
        window.addEventListener = jest.fn();
    });

    it('renders logo and navigation for unauthenticated user', () => {
        mockAuthService.isAuthenticated.mockReturnValue(false);
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        expect(screen.getByText('CvetOchey')).toBeInTheDocument();
        expect(screen.getByText('Каталог')).toBeInTheDocument();
        expect(screen.getByText('Войти')).toBeInTheDocument();
        expect(screen.queryByText('Выйти')).not.toBeInTheDocument();
    });

    it('renders user info and logout for authenticated user', () => {
        mockAuthService.isAuthenticated.mockReturnValue(true);
        mockAuthService.getUser.mockReturnValue({
            id: 1,
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe',
        });
        mockCartService.getTotalItems.mockReturnValue(3);

        render(<Header />);

        expect(screen.getByText('CvetOchey')).toBeInTheDocument();
        expect(screen.getByText('Каталог')).toBeInTheDocument();
        expect(screen.getByText('John Doe')).toBeInTheDocument();
        expect(screen.getByText('Корзина (3)')).toBeInTheDocument();
        expect(screen.getByText('Выйти')).toBeInTheDocument();
        expect(screen.queryByText('Войти')).not.toBeInTheDocument();
    });

    it('shows cart with zero items', () => {
        mockAuthService.isAuthenticated.mockReturnValue(true);
        mockAuthService.getUser.mockReturnValue({
            id: 1,
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe',
        });
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        expect(screen.getByText('Корзина (0)')).toBeInTheDocument();
    });

    it('handles logout correctly', async () => {
        mockAuthService.isAuthenticated.mockReturnValue(true);
        mockAuthService.getUser.mockReturnValue({
            id: 1,
            email: 'test@example.com',
            firstName: 'John',
            lastName: 'Doe',
        });
        mockAuthService.logout.mockResolvedValue(undefined);
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        const logoutButton = screen.getByText('Выйти');
        fireEvent.click(logoutButton);

        await waitFor(() => {
            expect(mockAuthService.logout).toHaveBeenCalled();
            expect(mockPush).toHaveBeenCalledWith('/');
        });
    });

    it('sets up event listeners on mount', () => {
        mockAuthService.isAuthenticated.mockReturnValue(false);
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        expect(window.addEventListener).toHaveBeenCalledWith('focus', expect.any(Function));
        expect(window.addEventListener).toHaveBeenCalledWith(
            'authStateChanged',
            expect.any(Function)
        );
        expect(window.addEventListener).toHaveBeenCalledWith(
            'cartStateChanged',
            expect.any(Function)
        );
    });

    it('cleans up event listeners on unmount', () => {
        mockAuthService.isAuthenticated.mockReturnValue(false);
        mockCartService.getTotalItems.mockReturnValue(0);

        const { unmount } = render(<Header />);
        unmount();

        expect(window.removeEventListener).toHaveBeenCalledWith('focus', expect.any(Function));
        expect(window.removeEventListener).toHaveBeenCalledWith(
            'authStateChanged',
            expect.any(Function)
        );
        expect(window.removeEventListener).toHaveBeenCalledWith(
            'cartStateChanged',
            expect.any(Function)
        );
    });

    it('applies custom className', () => {
        mockAuthService.isAuthenticated.mockReturnValue(false);
        mockCartService.getTotalItems.mockReturnValue(0);

        const { container } = render(<Header className="custom-header" />);

        expect(container.firstChild).toHaveClass('custom-header');
    });

    it('handles user with only first name', () => {
        mockAuthService.isAuthenticated.mockReturnValue(true);
        mockAuthService.getUser.mockReturnValue({
            id: 1,
            email: 'test@example.com',
            firstName: 'John',
            lastName: '',
        });
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        expect(screen.getByText('John')).toBeInTheDocument();
    });

    it('handles user with no name', () => {
        mockAuthService.isAuthenticated.mockReturnValue(true);
        mockAuthService.getUser.mockReturnValue({
            id: 1,
            email: 'test@example.com',
            firstName: '',
            lastName: '',
        });
        mockCartService.getTotalItems.mockReturnValue(0);

        render(<Header />);

        // Should show email or some fallback
        expect(screen.getByText('test@example.com')).toBeInTheDocument();
    });
});
