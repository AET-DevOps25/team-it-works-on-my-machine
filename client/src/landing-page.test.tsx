import { render } from '@testing-library/react'
import LandingPage from './landing-page'
import { test } from 'vitest'

test('renders without crashing', () => {
  render(<LandingPage />)
})
