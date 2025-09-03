import { API_CONFIG } from '@/shared/config/api';
import { AuthResponse, LoginRequest, RegisterRequest } from '../model/auth';

class AuthService {
    private static readonly TOKEN_KEY = 'auth_token';
    private static readonly REFRESH_TOKEN_KEY = 'refresh_token';
    private static readonly USER_KEY = 'user';

    async login(credentials: LoginRequest): Promise<AuthResponse> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.AUTH.LOGIN}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        const authResponse: AuthResponse = await response.json();
        this.saveAuthData(authResponse);
        return authResponse;
    }

    async register(userData: RegisterRequest): Promise<AuthResponse> {
        const response = await fetch(`${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.AUTH.REGISTER}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });

        if (!response.ok) {
            throw new Error('Registration failed');
        }

        const authResponse: AuthResponse = await response.json();
        this.saveAuthData(authResponse);
        return authResponse;
    }

    async logout(): Promise<void> {
        try {
            const token = this.getToken();
            if (token) {
                await fetch(`${API_CONFIG.FULL_API_URL}${API_CONFIG.ENDPOINTS.AUTH.LOGOUT}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                });
            }
        } catch (error) {
            console.error('Logout request failed:', error);
        } finally {
            this.clearAuthData();
        }
    }

    private saveAuthData(authResponse: AuthResponse): void {
        if (typeof window !== 'undefined') {
            localStorage.setItem(AuthService.TOKEN_KEY, authResponse.token);
            localStorage.setItem(AuthService.REFRESH_TOKEN_KEY, authResponse.refreshToken);
            localStorage.setItem(AuthService.USER_KEY, JSON.stringify(authResponse.user));
        }
    }

    private clearAuthData(): void {
        if (typeof window !== 'undefined') {
            localStorage.removeItem(AuthService.TOKEN_KEY);
            localStorage.removeItem(AuthService.REFRESH_TOKEN_KEY);
            localStorage.removeItem(AuthService.USER_KEY);
        }
    }

    getToken(): string | null {
        if (typeof window === 'undefined') return null;
        return localStorage.getItem(AuthService.TOKEN_KEY);
    }

    getRefreshToken(): string | null {
        if (typeof window === 'undefined') return null;
        return localStorage.getItem(AuthService.REFRESH_TOKEN_KEY);
    }

    getUser(): any | null {
        if (typeof window === 'undefined') return null;
        const userStr = localStorage.getItem(AuthService.USER_KEY);
        return userStr ? JSON.parse(userStr) : null;
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    getAuthHeaders(): Record<string, string> {
        const token = this.getToken();
        return token ? { Authorization: `Bearer ${token}` } : {};
    }
}

export const authService = new AuthService();
