'use client';
import { FC, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import styles from './login.module.scss';

const LoginPage: FC = () => {
    const router = useRouter();

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);
        const email = formData.get('email');
        const password = formData.get('password');

        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            router.push('/catalog');
        } else {
            // Handle errors
        }
    }

    return (
        <div className={styles.container}>
            <form onSubmit={handleSubmit} className={styles.form}>
                <input type="email" name="email" placeholder="Email" required />
                <input type="password" name="password" placeholder="Password" required />
                <button type="submit" className={styles.loginButton}>
                    Login
                </button>
            </form>
        </div>
    );
};

export default LoginPage;
