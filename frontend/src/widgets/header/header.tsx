'use client';
import { FC, HTMLProps, useEffect, useState } from 'react';
import { Props } from '@/shared/utils';
import { Logo } from '@/shared/components/logo';
import { authService, User } from '@/entities/auth';
import { cartService } from '@/entities/cart/api/cart-service';
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
    const [cartItemCount, setCartItemCount] = useState(0);
    const router = useRouter();

    useEffect(() => {
        const checkAuth = () => {
            const authenticated = authService.isAuthenticated();
            setIsAuthenticated(authenticated);
            if (authenticated) {
                setUser(authService.getUser());
            } else {
                setUser(null);
            }
        };

        const updateCartCount = () => {
            setCartItemCount(cartService.getTotalItems());
        };

        checkAuth();
        updateCartCount();

        // Listen for auth and cart state changes
        const handleAuthChange = () => checkAuth();
        const handleCartChange = () => updateCartCount();

        window.addEventListener('focus', checkAuth);
        window.addEventListener('authStateChanged', handleAuthChange);
        window.addEventListener('cartStateChanged', handleCartChange);

        return () => {
            window.removeEventListener('focus', checkAuth);
            window.removeEventListener('authStateChanged', handleAuthChange);
            window.removeEventListener('cartStateChanged', handleCartChange);
        };
    }, []);

    const handleLogout = async () => {
        await authService.logout();
        // No need to manually update state - the authStateChanged event will handle it
        router.push('/');
    };

    return (
        <header className={styles.header} {...rest}>
            <Link href="/catalog">
                <Logo className={clsx(styles.logo, className)} />
            </Link>
            <span className={styles.text}>Магазин цветов &#34;Цвет Очей&#34;</span>
            <div className={styles.userSection}>
                {/* Cart Button with Emoji - visible to all authenticated users */}
                {isAuthenticated && (
                    <Link href="/cart" className={styles.cartButton}>
                        <span className={styles.cartText}>🛒</span>
                        {cartItemCount > 0 && (
                            <span className={styles.cartBadge}>{cartItemCount}</span>
                        )}
                    </Link>
                )}

                {isAuthenticated && user ? (
                    <div className={styles.userInfo}>
                        <div className={styles.userDetails}>
                            <span className={styles.username}>{user.username}</span>
                            {user.userRole === 'ADMIN' && (
                                <span className={styles.adminBadge}>ADMIN</span>
                            )}
                        </div>
                        <div className={styles.userActions}>
                            <Link href="/user" className={styles.userLink}>
                                <Image
                                    src={userLogoSrc}
                                    alt="Страница пользователя"
                                    width={32}
                                    height={32}
                                />
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
