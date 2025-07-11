import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import LandingPage from './landing-page';

describe('LandingPage', () => {
  it('renders without crashing', () => {
    render(<LandingPage />);
  });
});
