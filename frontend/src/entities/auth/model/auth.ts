import { NumericId } from '@/shared/types/numeric-id';

export interface User {
    id: NumericId;
    email: string;
    username: string;
    userRole: 'CLIENT' | 'ADMIN';
}

export interface AuthResponse {
    token: string;
    refreshToken: string;
    user: User;
    tokenType: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    username: string;
    password: string;
    userRole?: 'CLIENT' | 'ADMIN';
}
