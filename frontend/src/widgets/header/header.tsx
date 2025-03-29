import { FC, HTMLProps } from 'react';
import { Props } from '@/shared/utils';
import { Logo } from '@/shared/components/logo';
import styles from './header.module.scss';

export type HeaderProps = Props<{}, false, HTMLProps<HTMLHeadingElement>>;
export const Header: FC<HeaderProps> = () => {
    return (
        <header className={styles.header}>
            <Logo className={styles.logo} />
            <span className={styles.text}>Магазин цветов &#34;Цвет Очей&#34;</span>
        </header>
    );
};
