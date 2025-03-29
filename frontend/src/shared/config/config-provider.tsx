import { FC } from 'react';
import { ConfigProvider as ConfigProviderComponent, ConfigProviderProps } from 'antd';
import { theme } from '@/shared/config/theme';

export const ConfigProvider: FC<ConfigProviderProps> = ({ children, ...rest }) => {
    return (
        <ConfigProviderComponent theme={theme} {...rest}>
            {children}
        </ConfigProviderComponent>
    );
};
