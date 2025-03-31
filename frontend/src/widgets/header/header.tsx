import { FC, HTMLProps } from 'react';
import { Props } from '@/shared/utils';
import { Logo } from '@/shared/components/logo';
import styles from './header.module.scss';
import clsx from 'clsx';
import userLogoSrc from '@/shared/assets/icons/user.svg';
import Link from 'next/link';
import Image from 'next/image';

export type HeaderProps = Props<{}, false, HTMLProps<HTMLHeadingElement>>;

export const Header: FC<HeaderProps> = ({ className, ...rest }) => {
    return (
        <header className={styles.header} {...rest}>
            <Link href="/catalog">
                <Logo className={clsx(styles.logo, className)} />
            </Link>
            <span className={styles.text}>Магазин цветов &#34;Цвет Очей&#34;</span>
            <div className={styles.userAvatar}>
                <Link href="/user">
                    <Image src={userLogoSrc} alt="Страница пользователя" width={56} height={56} />
                </Link>
            </div>
        </header>
    );
};
