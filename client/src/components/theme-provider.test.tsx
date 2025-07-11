import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { ThemeProvider } from './theme-provider';

describe('ThemeProvider', () => {
  it('renders children', () => {
    const { getByText } = render(
      <ThemeProvider>
        <div>Child</div>
      </ThemeProvider>
    );
    expect(getByText('Child')).toBeInTheDocument();
  });
});
