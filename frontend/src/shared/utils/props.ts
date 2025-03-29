import { CSSProperties, PropsWithChildren } from 'react';

export type IdProps = {
    id?: string;
};

export type ClassNameProps = {
    className?: string;
};

export type StyleProps = {
    style?: CSSProperties;
};

export type PropsWithRef<T> = T & { ref?: string };

export type DefaultProps = ClassNameProps & StyleProps & IdProps;

export type WithChildren<Props = {}> = PropsWithChildren<Props>;

export type Props<ComponentProps = {}, Children = true, HtmlElementProps = unknown> = PropsWithRef<
    Children extends false
        ? Omit<HtmlElementProps & DefaultProps & ComponentProps, 'children'>
        : Children extends true
          ? WithChildren<HtmlElementProps & DefaultProps & ComponentProps>
          : { children: Children } & HtmlElementProps & DefaultProps & ComponentProps
>;
