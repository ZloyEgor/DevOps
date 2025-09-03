'use client';
import { FC, FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '@/entities/auth';
import styles from './login.module.scss';

const LoginPage: FC = () => {
    const router = useRouter();
    const [error, setError] = useState<string>('');
    const [loading, setLoading] = useState(false);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError('');
        setLoading(true);

        const formData = new FormData(event.currentTarget);
        const email = formData.get('email') as string;
        const password = formData.get('password') as string;

        try {
            await authService.login({ email, password });
            router.push('/catalog');
        } catch (err) {
            setError('Invalid email or password');
            console.error('Login error:', err);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className={styles.container}>
            <form onSubmit={handleSubmit} className={styles.form}>
                {error && <div className={styles.error}>{error}</div>}
                <input 
                    type="email" 
                    name="email" 
                    placeholder="Email" 
                    required 
                    disabled={loading}
                />
                <input 
                    type="password" 
                    name="password" 
                    placeholder="Password" 
                    required 
                    disabled={loading}
                />
                <button type="submit" className={styles.loginButton} disabled={loading}>
                    {loading ? 'Signing in...' : 'Login'}
                </button>
            </form>
        </div>
    );
};

export default LoginPage;
