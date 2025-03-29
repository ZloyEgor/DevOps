import { FC, PropsWithChildren, ReactNode } from 'react';
import styles from './base-layout.module.scss';

export type BaseLayoutProps = PropsWithChildren<{ header?: ReactNode; footer?: ReactNode }>;
export const BaseLayout: FC<BaseLayoutProps> = ({ children, header, footer }) => {
    return (
        <div className={styles.wrapper}>
            {header ? header : null}
            <main className={styles.main}>{children}</main>
            {footer ? footer : null}
        </div>
    );
};
