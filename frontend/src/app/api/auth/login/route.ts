import type { NextApiRequest } from 'next';
import { login } from '@/features/login';
import { serialize } from 'cookie';

export async function POST(req: NextApiRequest) {
    try {
        const { email, password } = req.body;
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
