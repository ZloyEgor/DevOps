import { authService } from './auth-service';

// Mock fetch
global.fetch = jest.fn();

// Mock localStorage
const localStorageMock = {
    getItem: jest.fn(),
    setItem: jest.fn(),
    removeItem: jest.fn(),
    clear: jest.fn(),
};
Object.defineProperty(window, 'localStorage', {
    value: localStorageMock,
});

// Mock window events
Object.defineProperty(window, 'dispatchEvent', {
    value: jest.fn(),
});

describe('AuthService', () => {
    const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

    beforeEach(() => {
        jest.clearAllMocks();
        localStorageMock.getItem.mockClear();
        localStorageMock.setItem.mockClear();
        localStorageMock.removeItem.mockClear();
    });

    describe('login', () => {
        it('should login successfully and store token', async () => {
            const mockResponse = {
                token: 'jwt-token-123',
            };

            mockFetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockResponse,
            } as Response);

            const credentials = { email: 'test@example.com', password: 'password123' };
            await authService.login(credentials);

            expect(mockFetch).toHaveBeenCalledWith('/cvet-ochey/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials),
            });

            expect(localStorageMock.setItem).toHaveBeenCalledWith('auth_token', 'jwt-token-123');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('authStateChanged'));
        });

        it('should throw error on failed login', async () => {
            mockFetch.mockResolvedValueOnce({
                ok: false,
                status: 401,
            } as Response);

            const credentials = { email: 'test@example.com', password: 'wrongpassword' };

            await expect(authService.login(credentials)).rejects.toThrow('Login failed');
            expect(localStorageMock.setItem).not.toHaveBeenCalled();
        });

        it('should handle network errors', async () => {
            mockFetch.mockRejectedValueOnce(new Error('Network error'));

            const credentials = { email: 'test@example.com', password: 'password123' };

            await expect(authService.login(credentials)).rejects.toThrow('Network error');
            expect(localStorageMock.setItem).not.toHaveBeenCalled();
        });
    });

    describe('register', () => {
        it('should register successfully and store token', async () => {
            const mockResponse = {
                token: 'jwt-token-456',
            };

            mockFetch.mockResolvedValueOnce({
                ok: true,
                json: async () => mockResponse,
            } as Response);

            const userData = {
                email: 'test@example.com',
                password: 'password123',
                firstName: 'John',
                lastName: 'Doe',
                phoneNumber: '+1234567890',
            };

            await authService.register(userData);

            expect(mockFetch).toHaveBeenCalledWith('/cvet-ochey/api/v1/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
            });

            expect(localStorageMock.setItem).toHaveBeenCalledWith('auth_token', 'jwt-token-456');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('authStateChanged'));
        });

        it('should throw error on failed registration', async () => {
            mockFetch.mockResolvedValueOnce({
                ok: false,
                status: 400,
            } as Response);

            const userData = {
                email: 'existing@example.com',
                password: 'password123',
                firstName: 'John',
                lastName: 'Doe',
                phoneNumber: '+1234567890',
            };

            await expect(authService.register(userData)).rejects.toThrow('Registration failed');
            expect(localStorageMock.setItem).not.toHaveBeenCalled();
        });
    });

    describe('logout', () => {
        it('should logout and clear token', async () => {
            mockFetch.mockResolvedValueOnce({
                ok: true,
            } as Response);

            await authService.logout();

            expect(mockFetch).toHaveBeenCalledWith('/cvet-ochey/api/v1/auth/logout', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer null',
                },
            });

            expect(localStorageMock.removeItem).toHaveBeenCalledWith('auth_token');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('authStateChanged'));
        });

        it('should clear token even if API call fails', async () => {
            mockFetch.mockResolvedValueOnce({
                ok: false,
                status: 500,
            } as Response);

            await authService.logout();

            expect(localStorageMock.removeItem).toHaveBeenCalledWith('auth_token');
            expect(window.dispatchEvent).toHaveBeenCalledWith(new CustomEvent('authStateChanged'));
        });
    });

    describe('isAuthenticated', () => {
        it('should return true when token exists', () => {
            localStorageMock.getItem.mockReturnValue('jwt-token-123');

            expect(authService.isAuthenticated()).toBe(true);
            expect(localStorageMock.getItem).toHaveBeenCalledWith('auth_token');
        });

        it('should return false when no token exists', () => {
            localStorageMock.getItem.mockReturnValue(null);

            expect(authService.isAuthenticated()).toBe(false);
            expect(localStorageMock.getItem).toHaveBeenCalledWith('auth_token');
        });

        it('should return false when token is empty string', () => {
            localStorageMock.getItem.mockReturnValue('');

            expect(authService.isAuthenticated()).toBe(false);
        });
    });

    describe('getToken', () => {
        it('should return token from localStorage', () => {
            localStorageMock.getItem.mockReturnValue('jwt-token-789');

            expect(authService.getToken()).toBe('jwt-token-789');
            expect(localStorageMock.getItem).toHaveBeenCalledWith('auth_token');
        });

        it('should return null when no token exists', () => {
            localStorageMock.getItem.mockReturnValue(null);

            expect(authService.getToken()).toBeNull();
        });
    });

    describe('getUser', () => {
        it('should decode and return user from JWT token', () => {
            // Mock JWT token with user data (simplified)
            const mockToken =
                'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';
            localStorageMock.getItem.mockReturnValue(mockToken);

            const user = authService.getUser();

            expect(user).toBeDefined();
            expect(localStorageMock.getItem).toHaveBeenCalledWith('auth_token');
        });

        it('should return null when no token exists', () => {
            localStorageMock.getItem.mockReturnValue(null);

            expect(authService.getUser()).toBeNull();
        });

        it('should return null when token is invalid', () => {
            localStorageMock.getItem.mockReturnValue('invalid-token');

            expect(authService.getUser()).toBeNull();
        });
    });
});
