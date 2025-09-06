import { render, screen } from '@testing-library/react';
import UserPage from './page';
import '@testing-library/jest-dom';

describe('User Page', () => {
    it('renders user page content', () => {
        render(<UserPage />);

        expect(screen.getByText('User page')).toBeInTheDocument();
    });

    it('renders as a div element', () => {
        const { container } = render(<UserPage />);
        
        expect(container.firstChild).toBeTruthy();
        expect(container.firstChild?.textContent).toBe('User page');
    });
});
