import { render, screen, fireEvent } from '@testing-library/react';
import LoginPage from './page';
import '@testing-library/jest-dom';

// Mock next/navigation
jest.mock('next/navigation', () => ({
    useRouter: () => ({
        push: jest.fn(),
    }),
}));

// Mock auth service
jest.mock('@/entities/auth', () => ({
    authService: {
        login: jest.fn().mockResolvedValue(undefined),
    },
}));

describe('LoginPage', () => {
    it('renders login form with all required fields', () => {
        render(<LoginPage />);

        expect(screen.getByPlaceholderText('Email')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'Login' })).toBeInTheDocument();
    });

    it('requires email and password fields', () => {
        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');

        expect(emailInput).toHaveAttribute('required');
        expect(passwordInput).toHaveAttribute('required');
        expect(emailInput).toHaveAttribute('type', 'email');
        expect(passwordInput).toHaveAttribute('type', 'password');
    });

    it('handles form submission', () => {
        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');
        const loginButton = screen.getByRole('button', { name: 'Login' });

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });

        expect(emailInput).toHaveValue('test@example.com');
        expect(passwordInput).toHaveValue('password123');
        expect(loginButton).toBeEnabled();
    });

    it('renders form container with correct class', () => {
        const { container } = render(<LoginPage />);

        expect(container.querySelector('form')).toBeInTheDocument();
    });
});
