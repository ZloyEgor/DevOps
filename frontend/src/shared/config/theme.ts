import { ThemeConfig } from 'antd';

export const theme: ThemeConfig = {
    token: {
        colorPrimary: '#d3687f', // rose pink
        colorSuccess: '#a3c9a8', // soft green
        colorWarning: '#f3c77b', // light yellow
        colorError: '#e28e8e', // muted coral
        colorInfo: '#b6d4e3', // baby blue
        fontFamily: "'Playfair Display', serif",
        borderRadius: 8,
        colorBgBase: '#fefaf6', // warm off-white
        colorTextBase: '#3e3e3e',
    },
    components: {
        Button: {
            colorPrimary: '#d3687f',
            borderRadius: 100, // pill-like buttons
            fontWeight: 500,
        },
        Card: {
            colorBgContainer: '#ffffff',
            borderRadius: 12,
            boxShadow: '0 4px 12px rgba(211, 104, 127, 0.1)', // subtle rose shadow
        },
        Input: {
            colorBgContainer: '#fffdfd',
            borderRadius: 8,
        },
        Layout: {
            colorBgLayout: '#fffaf9',
            headerBg: '#f8e6ea',
            footerBg: '#f8e6ea',
        },
    },
};
