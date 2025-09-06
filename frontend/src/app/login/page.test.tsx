import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { useRouter } from 'next/navigation';
import LoginPage from './page';
import { authService } from '@/entities/auth';
import '@testing-library/jest-dom';

// Mock next/navigation
jest.mock('next/navigation', () => ({
    useRouter: jest.fn(),
}));

// Mock auth service
jest.mock('@/entities/auth', () => ({
    authService: {
        login: jest.fn(),
    },
}));

describe('LoginPage', () => {
    const mockPush = jest.fn();
    const mockAuthService = authService as jest.Mocked<typeof authService>;

    beforeEach(() => {
        (useRouter as jest.Mock).mockReturnValue({
            push: mockPush,
        });
        jest.clearAllMocks();
    });

    it('renders login form with all required fields', () => {
        render(<LoginPage />);

        expect(screen.getByPlaceholderText('Email')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'Login' })).toBeInTheDocument();
    });

    it('shows loading state during login', async () => {
        mockAuthService.login.mockImplementation(
            () => new Promise((resolve) => setTimeout(resolve, 100))
        );

        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');
        const loginButton = screen.getByRole('button', { name: 'Login' });

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });
        fireEvent.click(loginButton);

        expect(screen.getByText('Signing in...')).toBeInTheDocument();
        expect(loginButton).toBeDisabled();
        expect(emailInput).toBeDisabled();
        expect(passwordInput).toBeDisabled();
    });

    it('redirects to catalog on successful login', async () => {
        mockAuthService.login.mockResolvedValue(undefined);

        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');
        const loginButton = screen.getByRole('button', { name: 'Login' });

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(mockAuthService.login).toHaveBeenCalledWith({
                email: 'test@example.com',
                password: 'password123',
            });
            expect(mockPush).toHaveBeenCalledWith('/catalog');
        });
    });

    it('shows error message on login failure', async () => {
        mockAuthService.login.mockRejectedValue(new Error('Invalid credentials'));

        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');
        const loginButton = screen.getByRole('button', { name: 'Login' });

        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(screen.getByText('Invalid email or password')).toBeInTheDocument();
        });

        expect(mockPush).not.toHaveBeenCalled();
    });

    it('clears error message on new submission', async () => {
        mockAuthService.login.mockRejectedValueOnce(new Error('Invalid credentials'));

        render(<LoginPage />);

        const emailInput = screen.getByPlaceholderText('Email');
        const passwordInput = screen.getByPlaceholderText('Password');
        const loginButton = screen.getByRole('button', { name: 'Login' });

        // First failed attempt
        fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
        fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(screen.getByText('Invalid email or password')).toBeInTheDocument();
        });

        // Second attempt should clear error
        mockAuthService.login.mockResolvedValue(undefined);
        fireEvent.change(passwordInput, { target: { value: 'correctpassword' } });
        fireEvent.click(loginButton);

        await waitFor(() => {
            expect(screen.queryByText('Invalid email or password')).not.toBeInTheDocument();
        });
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

    it('handles form submission with empty fields gracefully', () => {
        render(<LoginPage />);

        const loginButton = screen.getByRole('button', { name: 'Login' });
        fireEvent.click(loginButton);

        // Should not call authService.login with empty fields due to HTML5 validation
        expect(mockAuthService.login).not.toHaveBeenCalled();
    });
});
