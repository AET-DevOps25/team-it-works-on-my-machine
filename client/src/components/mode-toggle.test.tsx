import '@testing-library/jest-dom'
import { render } from '@testing-library/react'
import { ModeToggle } from './mode-toggle'

describe('ModeToggle', () => {
  it('renders without crashing', () => {
    render(<ModeToggle />)
  })
})
