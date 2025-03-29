import { FC, PropsWithChildren, ReactNode } from 'react';

export type BaseLayoutProps = PropsWithChildren<{ header?: ReactNode; footer?: ReactNode }>;
export const BaseLayout: FC<BaseLayoutProps> = ({ children, header, footer }) => {
    return (
        <>
            {header ? header : null}
            <main>{children}</main>
            {footer ? footer : null}
        </>
    );
};
