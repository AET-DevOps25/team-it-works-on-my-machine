import { vi } from 'vitest'
Object.defineProperty(window, 'matchMedia', {
  value: vi.fn().mockImplementation(() => ({
    addListener: vi.fn(),
  })),
})
