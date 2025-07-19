import { render, screen } from '@testing-library/react'
import { vi, expect, test } from 'vitest'
import Routing from './Routing'

// Mock the LandingPage component
vi.mock('./landing-page', () => ({
  default: () => <div>Landing Page</div>,
}))

test('renders the LandingPage component at the root route', () => {
  render(<Routing />)
  expect(screen.getByText('Landing Page'))
})
