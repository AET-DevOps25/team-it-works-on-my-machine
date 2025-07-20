import { render } from '@testing-library/react'
import { ModeToggle } from './mode-toggle'
import { test } from 'vitest'

test('renders without crashing', () => {
  render(<ModeToggle />)
})
