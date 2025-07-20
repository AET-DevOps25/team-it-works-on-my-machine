import { render } from '@testing-library/react'
import Profile from './profile'
import { test } from 'vitest'

test('renders user info', () => {
  render(<Profile />)
})
