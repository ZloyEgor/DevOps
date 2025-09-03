'use client';
import { FC, HTMLProps, useEffect, useState } from 'react';
import { Props } from '@/shared/utils';
import { Logo } from '@/shared/components/logo';
import { authService, User } from '@/entities/auth';
import styles from './header.module.scss';
import clsx from 'clsx';
import userLogoSrc from '@/shared/assets/icons/user.svg';
import Link from 'next/link';
import Image from 'next/image';
import { useRouter } from 'next/navigation';

export type HeaderProps = Props<{}, false, HTMLProps<HTMLHeadingElement>>;

export const Header: FC<HeaderProps> = ({ className, ...rest }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const router = useRouter();

    useEffect(() => {
        const checkAuth = () => {
            const authenticated = authService.isAuthenticated();
            setIsAuthenticated(authenticated);
            if (authenticated) {
                setUser(authService.getUser());
            }
        };

        checkAuth();
        // Check auth status periodically or on window focus
        window.addEventListener('focus', checkAuth);
        return () => window.removeEventListener('focus', checkAuth);
    }, []);

    const handleLogout = async () => {
        await authService.logout();
        setUser(null);
        setIsAuthenticated(false);
        router.push('/');
    };

    return (
        <header className={styles.header} {...rest}>
            <Link href="/catalog">
                <Logo className={clsx(styles.logo, className)} />
            </Link>
            <span className={styles.text}>Магазин цветов &#34;Цвет Очей&#34;</span>
            <div className={styles.userSection}>
                {isAuthenticated && user ? (
                    <div className={styles.userInfo}>
                        <span className={styles.username}>{user.username}</span>
                        <div className={styles.userActions}>
                            <Link href="/user" className={styles.userLink}>
                                <Image src={userLogoSrc} alt="Страница пользователя" width={32} height={32} />
                            </Link>
                            <button onClick={handleLogout} className={styles.logoutButton}>
                                Выйти
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className={styles.authLinks}>
                        <Link href="/login" className={styles.loginLink}>
                            Войти
                        </Link>
                    </div>
                )}
            </div>
        </header>
    );
};
