import logoSrc from '@/shared/assets/icons/logo.svg';
import Image, { ImageProps } from 'next/image';
import { FC } from 'react';

export type LogoProps = Omit<ImageProps, 'src' | 'alt'>;

export const Logo: FC<LogoProps> = ({ ...props }) => {
    return <Image src={logoSrc} alt={'Cvetochey'} {...props} />;
};
