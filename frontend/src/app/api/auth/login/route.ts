import { login } from '@/features/login';
import { serialize } from 'cookie';
import { NextRequest } from 'next/server';

export async function POST(req: NextRequest) {
    try {
        const body = await req.json();

        const { email, password } = body;
        const result = await login('credentials', { email, password });

        if (result) {
            const cookie = serialize('session', 'YOUR SECRET TOKEN', {
                httpOnly: true,
                secure: process.env.NODE_ENV === 'production',
                maxAge: 60 * 60 * 24 * 7,
                path: '/',
            });

            return new Response('OK', { headers: { 'Set-Cookie': cookie }, status: 200 });
        }
        return new Response('WRONG');
    } catch (error) {
        // @ts-ignore
        if (error.type === 'CredentialsSignin') {
            return new Response('Invalid credentials.', { status: 401 });
        } else {
            return new Response('Something went wrong.', { status: 500 });
        }
    }
}
