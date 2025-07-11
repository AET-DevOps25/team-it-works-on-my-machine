import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import Routing from './Routing';

// Mock the LandingPage component
jest.mock('./components/page/landing-page', () => () => <div>Landing Page</div>);

describe('Routing', () => {
  it('renders the LandingPage component at the root route', () => {
    render(<Routing />);
    expect(screen.getByText('Landing Page')).toBeInTheDocument();
  });
});
